create_databases:
    docker-compose -up d

create tables:
    # products
    docker cp ./flyway/queries/V1__create_product_tables.sql drugstore_databases:/tmp/V1__create_product_tables.sql
    docker exec -i drugstore_databases psql -U postgres -d drugstore_products_db -f /tmp/V1__create_product_tables.sql

    # clients
    docker cp ./flyway/queries/V1__create_client_tables.sql drugstore_databases:/tmp/V1__create_client_tables.sql
    docker exec -i drugstore_databases psql -U postgres -d drugstore_clients_db -f /tmp/V1__create_client_tables.sql

    # address
    docker cp ./flyway/queries/V1__create_address_tables.sql drugstore_databases:/tmp/V1__create_address_tables.sql
    docker exec -i drugstore_databases psql -U postgres -d drugstore_addresses_db -f /tmp/V1__create_address_tables.sql

    # employees
    docker cp ./flyway/queries/V1__create_employee_tables.sql drugstore_databases:/tmp/V1__create_employee_tables.sql
    docker exec -i drugstore_databases psql -U postgres -d drugstore_employees_db -f /tmp/V1__create_employee_tables.sql

    # inventories
    docker cp ./flyway/queries/V1__create_inventory_tables.sql drugstore_databases:/tmp/V1__create_inventory_tables.sql
    docker exec -i drugstore_databases psql -U postgres -d drugstore_inventories_db -f /tmp/V1__create_inventory_tables.sql

    # sale
    docker cp ./flyway/queries/V1__create_sale_tables.sql drugstore_databases:/tmp/V1__create_sale_tables.sql
    docker exec -i drugstore_databases psql -U postgres -d drugstore_sales_db -f /tmp/V1__create_sale_tables.sql

    # user
    docker cp ./flyway/queries/V1__create_user_tables.sql drugstore_databases:/tmp/V1__create_user_tables.sql
    docker exec -i drugstore_databases psql -U postgres -d drugstore_users_db -f /tmp/V1__create_user_tables.sql

    # ecommerce_cart
    docker cp ./flyway/queries/V2__create__cart_tables.sql drugstore_databases:/tmp/V2__create_cart_tables.sql
    docker exec -i drugstore_databases psql -U postgres -d ecommerce_carts_db -f /tmp/V2__create_cart_tables.sql

   # ecommerce_order
    docker cp ./flyway/queries/V2__create__order_tables.sql drugstore_databases:/tmp/V2__create_order_tables.sql
    docker exec -i drugstore_databases psql -U postgres -d ecommerce_orders_db -f /tmp/V2__create_order_tables.sql

    # ecommerce_payment
    docker cp ./flyway/queries/V2__create__payment_tables.sql drugstore_databases:/tmp/V2__create_payment_tables.sql
    docker exec -i drugstore_databases psql -U postgres -d ecommerce_payments_db -f /tmp/V2__create_payment_tables.sql

    # ecommerce_sale
    docker cp ./flyway/queries/V2__create__sale_tables.sql drugstore_databases:/tmp/V2__create_sale_tables.sql
    docker exec -i drugstore_databases psql -U postgres -d ecommerce_sales_db -f /tmp/V2__create_sale_tables.sql
