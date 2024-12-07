import random
from faker import Faker

fake = Faker()

# Number of records for each table
num_accounts = 10
num_roles = 5
num_categories = 5
num_users = 10
num_documents = 15
num_downloads = 20
num_ratings = 20

# Generate Data
accounts = [
    (i + 1, fake.user_name(), fake.password(length=12))
    for i in range(num_accounts)
]

roles = [
    (i + 1, fake.job(), fake.text(max_nb_chars=200))
    for i in range(num_roles)
]

account_roles = [
    (random.randint(1, num_accounts), random.randint(1, num_roles))
    for _ in range(num_accounts)
]

categories = [
    (i + 1, fake.word(), fake.text(max_nb_chars=200))
    for i in range(num_categories)
]

users = [
    (i + 1, fake.date_of_birth(minimum_age=18, maximum_age=60), fake.name(),
     fake.phone_number(), random.choice([0, 1]), fake.address(), fake.email(),
     random.randint(1, num_accounts))
    for i in range(num_users)
]

documents = [
    (i + 1, random.randint(1, num_categories), random.randint(1, num_users),
     fake.image_url(), fake.sentence(nb_words=6), fake.text(max_nb_chars=500),
     random.choice([0, 1]), fake.date_time_this_year(), fake.date_time_this_year())
    for i in range(num_documents)
]

history_downloads = [
    (random.randint(1, num_documents), random.randint(1, num_accounts),
     fake.date_time_this_month())
    for _ in range(num_downloads)
]

ratings = [
    (random.randint(1, num_accounts), random.randint(1, num_documents),
     random.randint(1, 5))
    for _ in range(num_ratings)
]

# Generate SQL INSERT Statements
def generate_insert_statements(table_name, columns, values):
    sql = f"INSERT INTO {table_name} ({', '.join(columns)}) VALUES\n"
    sql += ",\n".join(
        str(tuple(v if isinstance(v, (int, float)) else f"N'{v}'" for v in row))
        for row in values
    )
    sql += ";\n"
    return sql

# Map table names to columns and data
tables = {
    "Account": {
        "columns": ["account_id", "username", "password"],
        "values": accounts
    },
    "Role": {
        "columns": ["role_id", "name", "description"],
        "values": roles
    },
    "Account_Role": {
        "columns": ["account_id", "role_id"],
        "values": account_roles
    },
    "Categroy": {
        "columns": ["category_id", "name", "description"],
        "values": categories
    },
    "User": {
        "columns": ["user_id", "dateOfBirth", "name", "phone", "gender", "address", "email", "account_id"],
        "values": users
    },
    "Document": {
        "columns": ["document_id", "category_id", "author_id", "thumbnail", "title", "content", "status", "create_at", "update_at"],
        "values": documents
    },
    "History_Download": {
        "columns": ["document_id", "account_id", "date"],
        "values": history_downloads
    },
    "Rating": {
        "columns": ["account_id", "document_id", "rate"],
        "values": ratings
    }
}

# Write SQL statements to a file
with open("mock_data.sql", "w") as f:
    for table_name, table_data in tables.items():
        f.write(generate_insert_statements(table_name, table_data["columns"], table_data["values"]))

print("SQL file 'mock_data.sql' created successfully!")
