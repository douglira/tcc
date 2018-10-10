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

-- SELECT UNNEST(get_pr_sellers(2)) as seller_id;
CREATE OR REPLACE FUNCTION get_pr_sellers(pr_id integer) RETURNS integer[]
AS $$ 
DECLARE
  pr_id ALIAS FOR $1;
  ppg_count integer;
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
      SELECT pr_products.product_item_id, pr_products.quantity 
      FROM pr_products
      WHERE pr_products.purchase_request_id = pr_id
    ) LOOP
      SELECT ARRAY(
        SELECT products.seller_id
        FROM product_items
        INNER JOIN products
        ON product_items.id = products.product_item_id
        INNER JOIN sellers
        ON products.seller_id = sellers.person_id
        WHERE products.status = CAST('ACTIVE' AS status_entity)
        AND products.product_item_id = rec_pr.product_item_id
        AND products.available_quantity >= rec_pr.quantity
      ) INTO sellers_ids;

      SELECT ppg_duplicated || sellers_ids INTO ppg_duplicated;

    END LOOP;

    SELECT ARRAY(
      SELECT UNNEST(ppg_duplicated) GROUP BY 1 HAVING COUNT(*) = pi_count
    ) INTO sellers_ids_result;
  END IF;
  
  RETURN sellers_ids_result;
END;
$$ LANGUAGE 'plpgsql';