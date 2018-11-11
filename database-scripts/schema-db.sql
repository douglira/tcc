-- TYPES

CREATE TYPE notification_resource AS ENUM ('PURCHASE_REQUEST', 'QUOTE', 'PAYMENT', 'ORDER');
CREATE TYPE notification_status AS ENUM ('PENDING', 'VIEWED');
CREATE TYPE pr_stage AS ENUM ('CREATION', 'UNDER_QUOTATION', 'CLOSED', 'EXPIRED', 'CANCELED');
CREATE TYPE product_situation AS ENUM ('LINKED', 'UNLINKED');
CREATE TYPE quote_status AS ENUM ('UNDER_REVIEW', 'ACCEPTED', 'DECLINED', 'EXPIRED');
CREATE TYPE shipment_method AS ENUM ('CUSTOM', 'FREE', 'LOCAL_PICK_UP');
CREATE TYPE shipment_status AS ENUM ('HANDLING', 'DISPATCHED_OUT', 'RETURNED', 'DELIVERED');
CREATE TYPE status_entity AS ENUM ('ACTIVE', 'INACTIVE');
CREATE TYPE users_role AS ENUM ('ADMIN', 'MODERATOR', 'COMMON');
CREATE TYPE order_status AS ENUM ('CONFIRMED', 'PAID', 'CANCELED');
									  
-- TABLES
											  
CREATE TABLE files (
	id SERIAL NOT NULL PRIMARY KEY,
	filename VARCHAR(255) NOT NULL,
	name VARCHAR(255) NOT NULL,
	size INTEGER NOT NULL,
	type VARCHAR(20) NOT NULL,
	url_path TEXT NOT NULL,
	created_at TIMESTAMP WITH TIME ZONE,
	updated_at TIMESTAMP WITH TIME ZONE
);
											  
CREATE TABLE users (
	id SERIAL NOT NULL PRIMARY KEY,
	email VARCHAR(255) NOT NULL,
	username VARCHAR(25) NOT NULL,
	display_name VARCHAR(255) NOT NULL,
	password VARCHAR(100) NOT NULL,
	role users_role NOT NULL,
	status status_entity NOT NULL,
	password_reset_token VARCHAR(255),
	password_expires_in TIMESTAMP WITH TIME ZONE,
	last_active TIMESTAMP WITH TIME ZONE,
	last_inactive TIMESTAMP WITH TIME ZONE,
	created_at TIMESTAMP WITH TIME ZONE,
	updated_at TIMESTAMP WITH TIME ZONE,
	status_changed_by INTEGER REFERENCES users (id),
	avatar_id INTEGER REFERENCES files (id)
);
											  
CREATE TABLE people (
	id SERIAL NOT NULL PRIMARY KEY,
	account_owner VARCHAR(255) NOT NULL,
	tel BIGINT NOT NULL,
	cnpj BIGINT NOT NULL,
	corporate_name VARCHAR(255) NOT NULL,
	state_registration VARCHAR(255) NOT NULL,
	created_at TIMESTAMP WITH TIME ZONE,
	updated_at TIMESTAMP WITH TIME ZONE,
	user_id INTEGER NOT NULL REFERENCES users (id)
);
											  
CREATE TABLE addresses (
	id SERIAL NOT NULL PRIMARY KEY,
	postal_code VARCHAR(255) NOT NULL,
	street VARCHAR(255) NOT NULL,
	district VARCHAR(255) NOT NULL,
	city VARCHAR(255) NOT NULL,
	province_code VARCHAR(255) NOT NULL,
	country_name VARCHAR(255) NOT NULL,
	building_number INTEGER NOT NULL,
	additional_data TEXT,
	created_at TIMESTAMP WITH TIME ZONE,
	updated_at TIMESTAMP WITH TIME ZONE,
	person_id INTEGER NOT NULL REFERENCES people (id)
);
											  
CREATE TABLE buyers (
	person_id SERIAL NOT NULL PRIMARY KEY,
	created_at TIMESTAMP WITH TIME ZONE,
	FOREIGN KEY (person_id) REFERENCES people (id)
);
											  
CREATE TABLE sellers (
	person_id SERIAL NOT NULL PRIMARY KEY,
	positive_sales_count INTEGER NOT NULL DEFAULT 0,
	negative_sales_count INTEGER NOT NULL DEFAULT 0,
	created_at TIMESTAMP WITH TIME ZONE,
	FOREIGN KEY (person_id) REFERENCES people (id)
);
											  
CREATE TABLE notifications (
	id SERIAL NOT NULL PRIMARY KEY,
	resource_type notification_resource NOT NULL,
	status notification_status NOT NULL,
	resource_id INTEGER,
	content TEXT,
	created_at TIMESTAMP WITH TIME ZONE,
	updated_at TIMESTAMP WITH TIME ZONE,
	from_user_id INTEGER REFERENCES users (id),
	to_user_id INTEGER NOT NULL REFERENCES users (id)
);
											  
CREATE TABLE categories (
	id SERIAL NOT NULL PRIMARY KEY,
	title VARCHAR(255) NOT NULL,
	is_last_child BOOLEAN NOT NULL,
	layer INTEGER NOT NULL,
	description TEXT,
	status status_entity NOT NULL,
	created_at TIMESTAMP WITH TIME ZONE,
	updated_at TIMESTAMP WITH TIME ZONE,
	parent_id INTEGER REFERENCES categories (id)
);
			
CREATE TABLE product_items (
	id SERIAL NOT NULL PRIMARY KEY,
	title VARCHAR(255) NOT NULL,
	views_count INTEGER NOT NULL DEFAULT 0,
	relevance INTEGER NOT NULL,
	base_price NUMERIC(15,2) NOT NULL,
	max_price NUMERIC(15,2) NOT NULL,
	min_price NUMERIC(15,2) NOT NULL,
	status status_entity NOT NULL,
	created_at TIMESTAMP WITH TIME ZONE,
	updated_at TIMESTAMP WITH TIME ZONE
);
											  
CREATE TABLE product_item_galleries (
	product_item_id INTEGER NOT NULL,
	picture_id INTEGER NOT NULL,
	PRIMARY KEY (product_item_id, picture_id),
	FOREIGN KEY (product_item_id) REFERENCES product_items (id),
	FOREIGN KEY (picture_id) REFERENCES files (id)
);
											  
CREATE TABLE products (
	id SERIAL NOT NULL PRIMARY KEY,
	title VARCHAR(255) NOT NULL,
	description TEXT,
	base_price NUMERIC(15,2) NOT NULL,
	sold_quantity INTEGER NOT NULL,
	available_quantity INTEGER NOT NULL,
	status status_entity NOT NULL,
	situation product_situation NOT NULL,
	created_at TIMESTAMP WITH TIME ZONE,
	updated_at TIMESTAMP WITH TIME ZONE,
	seller_id INTEGER REFERENCES sellers (person_id),
	category_id INTEGER REFERENCES categories (id),
	produtct_item_id INTEGER REFERENCES product_items (id)
);
											  
CREATE TABLE product_galleries (
	product_id INTEGER NOT NULL,
	picture_id INTEGER NOT NULL,
	PRIMARY KEY (product_id, picture_id),
	FOREIGN KEY (product_id) REFERENCES products (id),
	FOREIGN KEY (picture_id) REFERENCES files (id)
);
											  

CREATE TABLE purchase_requests (
	id SERIAL NOT NULL PRIMARY KEY,
	additional_data TEXT,
	stage pr_stage NOT NULL,
	due_date TIMESTAMP WITH TIME ZONE NOT NULL,
	total_amount NUMERIC(20,5) NOT NULL,
	views_count INTEGER NOT NULL DEFAULT 0,
	propagation_count INTEGER NOT NULL,
	quotes_visibility BOOLEAN NOT NULL,
	closed_at TIMESTAMP WITH TIME ZONE,
	created_at TIMESTAMP WITH TIME ZONE,
	updated_at TIMESTAMP WITH TIME ZONE,
	buyer_id INTEGER REFERENCES buyers (person_id)
);
											  
CREATE TABLE pr_products (
	purchase_request_id INTEGER NOT NULL,
	product_item_id INTEGER NOT NULL,
	quantity INTEGER NOT NULL,
	additional_spec TEXT,
	PRIMARY KEY (purchase_request_id, product_item_id),
	FOREIGN KEY (purchase_request_id) REFERENCES purchase_requests (id),
	FOREIGN KEY (product_item_id) REFERENCES product_items (id)
);
											  
CREATE TABLE quotes (
	id SERIAL NOT NULL PRIMARY KEY,
	additional_data TEXT,
	status quote_status NOT NULL,
	discount NUMERIC NOT NULL,
	total_amount NUMERIC(20,2) NOT NULL,
	expiration_date TIMESTAMP WITH TIME ZONE NOT NULL,
	created_at TIMESTAMP WITH TIME ZONE,
	updated_at TIMESTAMP WITH TIME ZONE,
	purchase_request_id INTEGER NOT NULL REFERENCES purchase_requests (id),
	seller_id INTEGER NOT NULL REFERENCES sellers (person_id)
);
											  
CREATE TABLE quote_products (
	quote_id INTEGER NOT NULL,
	product_id INTEGER NOT NULL,
	quantity INTEGER NOT NULL,
	sale_price NUMERIC(15,2) NOT NULL,
	PRIMARY KEY (quote_id, product_id),
	FOREIGN KEY (quote_id) REFERENCES quotes (id),
	FOREIGN KEY (product_id) REFERENCES products (id)
);
											  
CREATE TABLE shipments (
	id SERIAL NOT NULL PRIMARY KEY,
	cost NUMERIC(10,2) NOT NULL,
	status shipment_status NOT NULL,
	method shipment_method NOT NULL,
	estimated_time DATE,
	created_at TIMESTAMP WITH TIME ZONE,
	updated_at TIMESTAMP WITH TIME ZONE,
	receiver_address_id INTEGER REFERENCES addresses (id),
	quote_id INTEGER NOT NULL REFERENCES quotes (id)
);
											  
CREATE TABLE orders (
	id SERIAL NOT NULL PRIMARY KEY,
	status order_status NOT NULL,
	total_amount NUMERIC(30,2) NOT NULL,
	created_at TIMESTAMP WITH TIME ZONE,
	updated_at TIMESTAMP WITH TIME ZONE,
	quote_id INTEGER NOT NULL REFERENCES quotes (id),
	shipment_id INTEGER NOT NULL REFERENCES shipments (id)
);
											  
-- INDEXES
											  
CREATE INDEX notifications_created_at ON notifications (created_at);
CREATE INDEX notifications_from_to_users ON notifications (from_user_id, to_user_id);
CREATE INDEX notifications_from_users ON notifications (from_user_id);
CREATE INDEX notifications_status_to_users ON notifications (status, to_user_id);
CREATE INDEX notifications_to_users ON notifications (to_user_id);
											  
CREATE INDEX pr_products_pkey_pi ON pr_products (product_item_id);
CREATE INDEX pr_products_pkey_pr ON pr_products (purchase_request_id);
CREATE INDEX pr_products_pkeys ON pr_products (product_item_id, purchase_request_id);
											  
CREATE INDEX purchase_requests_buyer_stage ON purchase_requests (buyer_id, stage);
											  
CREATE INDEX quote_products_index ON quote_products (quote_id, product_id);

CREATE INDEX quote_id_pr_seller ON quotes (id, purchase_request_id, seller_id);
CREATE INDEX quote_pr_seller ON quotes (purchase_request_id, seller_id);
CREATE INDEX quote_pr ON quotes (purchase_request_id);					

-- SEQUENCES

CREATE SEQUENCE pr_sequence START 10000 MINVALUE 10000 OWNED BY purchase_requests.id;

-- FUNCTIONS

CREATE OR REPLACE FUNCTION func_insert_new_category(title varchar(255), description text, layer integer, parent_id integer) 
RETURNS void AS $$
DECLARE
  title ALIAS FOR $1;
  description ALIAS FOR $2;
  layer ALIAS FOR $3;
  parent ALIAS FOR $4;
  status status_entity;
  is_last_child BOOLEAN;
BEGIN
  status := 'ACTIVE';
  is_last_child := true;
  INSERT INTO categories ("title", "description", "is_last_child", "layer", "parent_id", "status", "created_at")
  VALUES (title, description, is_last_child, layer, parent, status, NOW());
   
  IF parent_id IS NULL THEN
    RETURN;
  ELSE
    UPDATE "categories" SET "is_last_child" = false WHERE "id" = parent;
  END IF;
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION func_toggle_status_categories (categoryId integer, status status_entity) RETURNS void AS $$
DECLARE
  categoryId ALIAS FOR $1;
  categoryStatus ALIAS FOR $2;
  returnedId integer;
  rowSelect record;
BEGIN
  UPDATE categories SET status = categoryStatus, updated_at = NOW() WHERE id = categoryId RETURNING id INTO returnedId;
  
  IF returnedId > 0 THEN
    FOR rowSelect IN SELECT id FROM categories WHERE parent_id = returnedId
    LOOP
      PERFORM * FROM func_toggle_status_categories(rowSelect.id, categoryStatus);
    END LOOP;
  END IF;
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION get_pr_sellers(pr_id integer) 
RETURNS TABLE (person_id integer, created_at timestamp with time zone, positive_sales_count integer, negative_sales_count integer)
AS $$ 
DECLARE
  pr_id ALIAS FOR $1;
  ppg_duplicated integer[][];
  sellers_ids integer[];
  pi_count integer;
  rec_pr RECORD;
  sellers_ids_result integer[];
BEGIN

  SELECT COUNT(pr_products.product_item_id)
  FROM pr_products
  WHERE pr_products.purchase_request_id = pr_id INTO pi_count;
  
  IF pi_count > 0 THEN
  
    FOR rec_pr IN (
      SELECT purchase_requests.buyer_id, pr_products.product_item_id, pr_products.quantity 
      FROM pr_products
      INNER JOIN purchase_requests
      ON purchase_requests.id = pr_products.purchase_request_id
      WHERE pr_products.purchase_request_id = pr_id
    ) LOOP
      SELECT ARRAY(
        SELECT products.seller_id
        FROM product_items
        INNER JOIN products
        ON product_items.id = products.product_item_id
        INNER JOIN sellers
        ON products.seller_id = sellers.person_id
        INNER JOIN people 
        ON sellers.person_id = people.id
        INNER JOIN users
        ON people.user_id = users.id
        WHERE products.status = CAST('ACTIVE' AS status_entity)
        AND users.status = CAST('ACTIVE' as status_entity) 
        AND products.product_item_id = rec_pr.product_item_id
        AND products.available_quantity >= rec_pr.quantity
        AND rec_pr.buyer_id <> sellers.person_id
      ) INTO sellers_ids;

      SELECT ppg_duplicated || sellers_ids INTO ppg_duplicated;

    END LOOP;

    SELECT ARRAY(
      SELECT UNNEST(ppg_duplicated) GROUP BY 1 HAVING COUNT(*) = pi_count
    ) INTO sellers_ids_result;
  END IF;
  RETURN QUERY SELECT s.person_id, s.created_at, s.positive_sales_count, s.negative_sales_count 
    FROM sellers AS s WHERE s.person_id = ANY(sellers_ids_result::integer[]);
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION pr_update_views(pr_id integer)
RETURNS void AS $$
DECLARE
  pr_id ALIAS FOR $1;
  final_views_count integer;
  row_select record;
BEGIN
  FOR row_select IN SELECT views_count FROM purchase_requests WHERE id = pr_id
  LOOP
    final_views_count := row_select.views_count + 1;
    UPDATE purchase_requests SET views_count = final_views_count WHERE id = pr_id;
  END LOOP;
END;
$$ LANGUAGE 'plpgsql';
		
