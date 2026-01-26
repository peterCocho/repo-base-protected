import psycopg2

# Conectar a la base de datos
conn = psycopg2.connect(
    host="localhost",
    port="5432",
    database="churninsight",
    user="postgres",
    password="admin"
)

cursor = conn.cursor()

# Verificar qué géneros existen actualmente
cursor.execute("SELECT DISTINCT gender FROM customers WHERE gender IS NOT NULL")
current_genders = cursor.fetchall()
print("Géneros actuales en la base de datos:")
for gender in current_genders:
    print(f"  {gender[0]}")

# Migrar 'M' a 'Male' y 'F' a 'Female'
print("\nMigrando datos de género...")

cursor.execute("UPDATE customers SET gender = 'Male' WHERE gender = 'M'")
male_updated = cursor.rowcount

cursor.execute("UPDATE customers SET gender = 'Female' WHERE gender = 'F'")
female_updated = cursor.rowcount

conn.commit()

print(f"Actualizados {male_updated} registros de 'M' a 'Male'")
print(f"Actualizados {female_updated} registros de 'F' a 'Female'")

# Verificar el resultado
cursor.execute("SELECT gender, COUNT(*) FROM customers WHERE gender IS NOT NULL GROUP BY gender")
final_counts = cursor.fetchall()
print("\nDistribución final de géneros:")
for gender, count in final_counts:
    print(f"  {gender}: {count}")

cursor.close()
conn.close()

print("\n¡Migración de géneros completada!")