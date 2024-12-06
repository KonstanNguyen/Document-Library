import pyodbc

connection_string = ("DRIVER={SQL Server};"
                         "SERVER=LAPTOP-KJ4L2LKH;"
                         "DATABASE=HTTM;"
                         "UID=sa;"
                         "PWD=Ntn@2003;")


try:
    conn = pyodbc.connect(connection_string)
    print("Connection successful!")
except Exception as e:
    print(f"Error: {e}")