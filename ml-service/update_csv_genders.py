import pandas as pd

# Actualizar customers_drama.csv
df = pd.read_csv('customers_drama.csv')
df['gender'] = df['gender'].replace({'M': 'Male', 'F': 'Female'})
df.to_csv('customers_drama.csv', index=False)
print('customers_drama.csv actualizado')

# Actualizar paquete_1.csv
df1 = pd.read_csv('paquete_1.csv')
df1['gender'] = df1['gender'].replace({'M': 'Male', 'F': 'Female'})
df1.to_csv('paquete_1.csv', index=False)
print('paquete_1.csv actualizado')

# Actualizar paquete_2.csv
df2 = pd.read_csv('paquete_2.csv')
df2['gender'] = df2['gender'].replace({'M': 'Male', 'F': 'Female'})
df2.to_csv('paquete_2.csv', index=False)
print('paquete_2.csv actualizado')

# Actualizar paquete_3.csv
df3 = pd.read_csv('paquete_3.csv')
df3['gender'] = df3['gender'].replace({'M': 'Male', 'F': 'Female'})
df3.to_csv('paquete_3.csv', index=False)
print('paquete_3.csv actualizado')

print('Todos los archivos CSV han sido actualizados')