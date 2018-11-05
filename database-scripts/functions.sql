-- ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

-- ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

-- ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

-- SELECT * FROM get_pr_sellers(10042);
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