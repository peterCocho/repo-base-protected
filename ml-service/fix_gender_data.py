import pandas as pd
import numpy as np
import psycopg2
from psycopg2.extras import execute_values

# Leer el archivo CSV original
customers_df = pd.read_csv('customers_drama.csv')
print(f"Customers en CSV: {len(customers_df)}")
print("Distribución de género en CSV:")
print(customers_df['gender'].value_counts(dropna=False))

# Conectar a la base de datos
conn = psycopg2.connect(
    host="localhost",
    port="5432",
    database="churninsight",
    user="postgres",
    password="admin"
)

cursor = conn.cursor()

# Ver qué customers existen en la base de datos
cursor.execute("""
    SELECT c.customer_id, c.gender, u.email
    FROM customers c
    LEFT JOIN users u ON u.id = c.user_id
    WHERE u.email = 'test1@example.com'
""")

db_customers = cursor.fetchall()
print(f"\nCustomers en BD para test1@example.com: {len(db_customers)}")

gender_counts = {}
null_gender_count = 0

for customer in db_customers:
    customer_id, gender, email = customer
    if gender is None:
        null_gender_count += 1
    else:
        gender_counts[gender] = gender_counts.get(gender, 0) + 1

print(f"Customers con gender definido: {len(db_customers) - null_gender_count}")
print(f"Customers con gender NULL: {null_gender_count}")
print(f"Distribución de gender: {gender_counts}")

# Si hay customers con gender NULL, asignar géneros aleatorios
if null_gender_count > 0:
    print(f"\nAsignando géneros aleatorios a {null_gender_count} customers...")

    # Obtener IDs de customers con gender NULL
    cursor.execute("""
        SELECT c.id
        FROM customers c
        LEFT JOIN users u ON u.id = c.user_id
        WHERE u.email = 'test1@example.com' AND c.gender IS NULL
    """)

    null_gender_ids = [row[0] for row in cursor.fetchall()]

    # Asignar géneros aleatorios (aproximadamente 50/50)
    genders = ['M', 'F'] * (len(null_gender_ids) // 2 + 1)
    genders = genders[:len(null_gender_ids)]
    np.random.shuffle(genders)

    # Actualizar la base de datos
    update_data = list(zip(genders, null_gender_ids))
    execute_values(cursor,
        "UPDATE customers SET gender = %s WHERE id = %s",
        update_data
    )

    conn.commit()
    print(f"Actualizados {len(update_data)} customers con géneros aleatorios")

    # Verificar el resultado
    cursor.execute("""
        SELECT c.gender, COUNT(*)
        FROM customers c
        LEFT JOIN users u ON u.id = c.user_id
        WHERE u.email = 'test1@example.com'
        GROUP BY c.gender
    """)

    final_counts = cursor.fetchall()
    print("Distribución final de gender:")
    for gender, count in final_counts:
        print(f"  {gender}: {count}")

cursor.close()
conn.close()

print("\n¡Datos de gender actualizados!")