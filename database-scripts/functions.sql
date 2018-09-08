-- ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

CREATE OR REPLACE FUNCTION func_insert_new_category(title varchar(255), description text, layer integer, parent_id integer) RETURNS void AS $$
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
  UPDATE categories SET status = categoryStatus WHERE id = categoryId RETURNING id INTO returnedId;
  
  IF returnedId > 0 THEN
    FOR rowSelect IN SELECT id FROM categories WHERE parent_id = returnedId
    LOOP
      PERFORM * FROM func_toggle_status_categories(rowSelect.id, categoryStatus);
    END LOOP;
  END IF;
END;
$$ LANGUAGE 'plpgsql';

-- ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////