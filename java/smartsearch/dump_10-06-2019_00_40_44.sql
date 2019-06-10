--
-- PostgreSQL database cluster dump
--

SET default_transaction_read_only = off;

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

--
-- Drop databases (except postgres and template1)
--

DROP DATABASE douglas;
DROP DATABASE smartsearch;
DROP DATABASE v2saude;




--
-- Drop roles
--

DROP ROLE douglas;


--
-- Roles
--

CREATE ROLE douglas;
ALTER ROLE douglas WITH SUPERUSER INHERIT CREATEROLE CREATEDB LOGIN REPLICATION BYPASSRLS PASSWORD 'md55918c89f0cff87b1513931f04eefadc4';






--
-- PostgreSQL database dump
--

-- Dumped from database version 11.1 (Debian 11.1-1.pgdg90+1)
-- Dumped by pg_dump version 11.1 (Debian 11.1-1.pgdg90+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

UPDATE pg_catalog.pg_database SET datistemplate = false WHERE datname = 'template1';
DROP DATABASE template1;
--
-- Name: template1; Type: DATABASE; Schema: -; Owner: douglas
--

CREATE DATABASE template1 WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8';


ALTER DATABASE template1 OWNER TO douglas;

\connect template1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: DATABASE template1; Type: COMMENT; Schema: -; Owner: douglas
--

COMMENT ON DATABASE template1 IS 'default template for new databases';


--
-- Name: template1; Type: DATABASE PROPERTIES; Schema: -; Owner: douglas
--

ALTER DATABASE template1 IS_TEMPLATE = true;


\connect template1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: DATABASE template1; Type: ACL; Schema: -; Owner: douglas
--

REVOKE CONNECT,TEMPORARY ON DATABASE template1 FROM PUBLIC;
GRANT CONNECT ON DATABASE template1 TO PUBLIC;


--
-- PostgreSQL database dump complete
--

--
-- PostgreSQL database dump
--

-- Dumped from database version 11.1 (Debian 11.1-1.pgdg90+1)
-- Dumped by pg_dump version 11.1 (Debian 11.1-1.pgdg90+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: douglas; Type: DATABASE; Schema: -; Owner: douglas
--

CREATE DATABASE douglas WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8';


ALTER DATABASE douglas OWNER TO douglas;

\connect douglas

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- PostgreSQL database dump complete
--

--
-- PostgreSQL database dump
--

-- Dumped from database version 11.1 (Debian 11.1-1.pgdg90+1)
-- Dumped by pg_dump version 11.1 (Debian 11.1-1.pgdg90+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE postgres;
--
-- Name: postgres; Type: DATABASE; Schema: -; Owner: douglas
--

CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8';


ALTER DATABASE postgres OWNER TO douglas;

\connect postgres

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: DATABASE postgres; Type: COMMENT; Schema: -; Owner: douglas
--

COMMENT ON DATABASE postgres IS 'default administrative connection database';


--
-- PostgreSQL database dump complete
--

--
-- PostgreSQL database dump
--

-- Dumped from database version 11.1 (Debian 11.1-1.pgdg90+1)
-- Dumped by pg_dump version 11.1 (Debian 11.1-1.pgdg90+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: smartsearch; Type: DATABASE; Schema: -; Owner: douglas
--

CREATE DATABASE smartsearch WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8';


ALTER DATABASE smartsearch OWNER TO douglas;

\connect smartsearch

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: notification_resource; Type: TYPE; Schema: public; Owner: douglas
--

CREATE TYPE public.notification_resource AS ENUM (
    'PURCHASE_REQUEST',
    'QUOTE',
    'PAYMENT',
    'ORDER'
);


ALTER TYPE public.notification_resource OWNER TO douglas;

--
-- Name: notification_status; Type: TYPE; Schema: public; Owner: douglas
--

CREATE TYPE public.notification_status AS ENUM (
    'PENDING',
    'VIEWED'
);


ALTER TYPE public.notification_status OWNER TO douglas;

--
-- Name: order_status; Type: TYPE; Schema: public; Owner: douglas
--

CREATE TYPE public.order_status AS ENUM (
    'CONFIRMED',
    'PAID',
    'CANCELED'
);


ALTER TYPE public.order_status OWNER TO douglas;

--
-- Name: pr_stage; Type: TYPE; Schema: public; Owner: douglas
--

CREATE TYPE public.pr_stage AS ENUM (
    'CREATION',
    'UNDER_QUOTATION',
    'CLOSED',
    'EXPIRED',
    'CANCELED'
);


ALTER TYPE public.pr_stage OWNER TO douglas;

--
-- Name: product_situation; Type: TYPE; Schema: public; Owner: douglas
--

CREATE TYPE public.product_situation AS ENUM (
    'LINKED',
    'UNLINKED'
);


ALTER TYPE public.product_situation OWNER TO douglas;

--
-- Name: quote_status; Type: TYPE; Schema: public; Owner: douglas
--

CREATE TYPE public.quote_status AS ENUM (
    'UNDER_REVIEW',
    'ACCEPTED',
    'DECLINED',
    'EXPIRED'
);


ALTER TYPE public.quote_status OWNER TO douglas;

--
-- Name: shipment_method; Type: TYPE; Schema: public; Owner: douglas
--

CREATE TYPE public.shipment_method AS ENUM (
    'CUSTOM',
    'FREE',
    'LOCAL_PICK_UP'
);


ALTER TYPE public.shipment_method OWNER TO douglas;

--
-- Name: shipment_status; Type: TYPE; Schema: public; Owner: douglas
--

CREATE TYPE public.shipment_status AS ENUM (
    'HANDLING',
    'DISPATCHED_OUT',
    'RETURNED',
    'DELIVERED'
);


ALTER TYPE public.shipment_status OWNER TO douglas;

--
-- Name: status_entity; Type: TYPE; Schema: public; Owner: douglas
--

CREATE TYPE public.status_entity AS ENUM (
    'ACTIVE',
    'INACTIVE'
);


ALTER TYPE public.status_entity OWNER TO douglas;

--
-- Name: users_role; Type: TYPE; Schema: public; Owner: douglas
--

CREATE TYPE public.users_role AS ENUM (
    'ADMIN',
    'MODERATOR',
    'COMMON'
);


ALTER TYPE public.users_role OWNER TO douglas;

--
-- Name: func_insert_new_category(character varying, text, integer, integer); Type: FUNCTION; Schema: public; Owner: douglas
--

CREATE FUNCTION public.func_insert_new_category(title character varying, description text, layer integer, parent_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $_$
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
$_$;


ALTER FUNCTION public.func_insert_new_category(title character varying, description text, layer integer, parent_id integer) OWNER TO douglas;

--
-- Name: func_toggle_status_categories(integer, public.status_entity); Type: FUNCTION; Schema: public; Owner: douglas
--

CREATE FUNCTION public.func_toggle_status_categories(categoryid integer, status public.status_entity) RETURNS void
    LANGUAGE plpgsql
    AS $_$
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
$_$;


ALTER FUNCTION public.func_toggle_status_categories(categoryid integer, status public.status_entity) OWNER TO douglas;

--
-- Name: get_pr_sellers(integer); Type: FUNCTION; Schema: public; Owner: douglas
--

CREATE FUNCTION public.get_pr_sellers(pr_id integer) RETURNS TABLE(person_id integer, created_at timestamp with time zone, positive_sales_count integer, negative_sales_count integer)
    LANGUAGE plpgsql
    AS $_$ 
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
$_$;


ALTER FUNCTION public.get_pr_sellers(pr_id integer) OWNER TO douglas;

--
-- Name: pr_update_views(integer); Type: FUNCTION; Schema: public; Owner: douglas
--

CREATE FUNCTION public.pr_update_views(pr_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $_$
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
$_$;


ALTER FUNCTION public.pr_update_views(pr_id integer) OWNER TO douglas;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: addresses; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.addresses (
    id integer NOT NULL,
    postal_code character varying(255) NOT NULL,
    street character varying(255) NOT NULL,
    district character varying(255) NOT NULL,
    city character varying(255) NOT NULL,
    province_code character varying(255) NOT NULL,
    country_name character varying(255) NOT NULL,
    building_number integer NOT NULL,
    additional_data text,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    person_id integer NOT NULL
);


ALTER TABLE public.addresses OWNER TO douglas;

--
-- Name: addresses_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.addresses_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.addresses_id_seq OWNER TO douglas;

--
-- Name: addresses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.addresses_id_seq OWNED BY public.addresses.id;


--
-- Name: buyers; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.buyers (
    person_id integer NOT NULL,
    created_at timestamp with time zone
);


ALTER TABLE public.buyers OWNER TO douglas;

--
-- Name: buyers_person_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.buyers_person_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.buyers_person_id_seq OWNER TO douglas;

--
-- Name: buyers_person_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.buyers_person_id_seq OWNED BY public.buyers.person_id;


--
-- Name: categories; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.categories (
    id integer NOT NULL,
    title character varying(255) NOT NULL,
    is_last_child boolean NOT NULL,
    layer integer NOT NULL,
    description text,
    status public.status_entity NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    parent_id integer
);


ALTER TABLE public.categories OWNER TO douglas;

--
-- Name: categories_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.categories_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.categories_id_seq OWNER TO douglas;

--
-- Name: categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.categories_id_seq OWNED BY public.categories.id;


--
-- Name: files; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.files (
    id integer NOT NULL,
    file_path character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    size integer NOT NULL,
    type character varying(20) NOT NULL,
    url_path text NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    subtype character varying(20) NOT NULL
);


ALTER TABLE public.files OWNER TO douglas;

--
-- Name: files_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.files_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.files_id_seq OWNER TO douglas;

--
-- Name: files_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.files_id_seq OWNED BY public.files.id;


--
-- Name: notifications; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.notifications (
    id integer NOT NULL,
    resource_type public.notification_resource NOT NULL,
    status public.notification_status NOT NULL,
    resource_id integer,
    content text,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    from_user_id integer,
    to_user_id integer NOT NULL
);


ALTER TABLE public.notifications OWNER TO douglas;

--
-- Name: notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.notifications_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.notifications_id_seq OWNER TO douglas;

--
-- Name: notifications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.notifications_id_seq OWNED BY public.notifications.id;


--
-- Name: orders; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.orders (
    id integer NOT NULL,
    status public.order_status NOT NULL,
    total_amount numeric(30,2) NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    quote_id integer NOT NULL,
    shipment_id integer NOT NULL
);


ALTER TABLE public.orders OWNER TO douglas;

--
-- Name: orders_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.orders_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.orders_id_seq OWNER TO douglas;

--
-- Name: orders_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.orders_id_seq OWNED BY public.orders.id;


--
-- Name: people; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.people (
    id integer NOT NULL,
    account_owner character varying(255) NOT NULL,
    tel bigint NOT NULL,
    cnpj bigint NOT NULL,
    corporate_name character varying(255) NOT NULL,
    state_registration character varying(255) NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    user_id integer NOT NULL
);


ALTER TABLE public.people OWNER TO douglas;

--
-- Name: people_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.people_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.people_id_seq OWNER TO douglas;

--
-- Name: people_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.people_id_seq OWNED BY public.people.id;


--
-- Name: pr_products; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.pr_products (
    purchase_request_id integer NOT NULL,
    product_item_id integer NOT NULL,
    quantity integer NOT NULL,
    additional_spec text
);


ALTER TABLE public.pr_products OWNER TO douglas;

--
-- Name: purchase_requests; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.purchase_requests (
    id integer NOT NULL,
    additional_data text,
    stage public.pr_stage NOT NULL,
    due_date timestamp with time zone NOT NULL,
    total_amount numeric(20,5) NOT NULL,
    views_count integer DEFAULT 0 NOT NULL,
    propagation_count integer NOT NULL,
    quotes_visibility boolean NOT NULL,
    closed_at timestamp with time zone,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    buyer_id integer
);


ALTER TABLE public.purchase_requests OWNER TO douglas;

--
-- Name: pr_sequence; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.pr_sequence
    START WITH 10000
    INCREMENT BY 1
    MINVALUE 10000
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.pr_sequence OWNER TO douglas;

--
-- Name: pr_sequence; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.pr_sequence OWNED BY public.purchase_requests.id;


--
-- Name: product_galleries; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.product_galleries (
    product_id integer NOT NULL,
    picture_id integer NOT NULL
);


ALTER TABLE public.product_galleries OWNER TO douglas;

--
-- Name: product_item_galleries; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.product_item_galleries (
    product_item_id integer NOT NULL,
    picture_id integer NOT NULL
);


ALTER TABLE public.product_item_galleries OWNER TO douglas;

--
-- Name: product_items; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.product_items (
    id integer NOT NULL,
    title character varying(255) NOT NULL,
    views_count integer DEFAULT 0 NOT NULL,
    relevance integer NOT NULL,
    base_price numeric(15,2) NOT NULL,
    max_price numeric(15,2) NOT NULL,
    min_price numeric(15,2) NOT NULL,
    status public.status_entity NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone
);


ALTER TABLE public.product_items OWNER TO douglas;

--
-- Name: product_items_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.product_items_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.product_items_id_seq OWNER TO douglas;

--
-- Name: product_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.product_items_id_seq OWNED BY public.product_items.id;


--
-- Name: products; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.products (
    id integer NOT NULL,
    title character varying(255) NOT NULL,
    description text,
    base_price numeric(15,2) NOT NULL,
    sold_quantity integer NOT NULL,
    available_quantity integer NOT NULL,
    status public.status_entity NOT NULL,
    situation public.product_situation NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    seller_id integer,
    category_id integer,
    product_item_id integer
);


ALTER TABLE public.products OWNER TO douglas;

--
-- Name: products_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.products_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.products_id_seq OWNER TO douglas;

--
-- Name: products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.products_id_seq OWNED BY public.products.id;


--
-- Name: purchase_requests_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.purchase_requests_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.purchase_requests_id_seq OWNER TO douglas;

--
-- Name: purchase_requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.purchase_requests_id_seq OWNED BY public.purchase_requests.id;


--
-- Name: quote_products; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.quote_products (
    quote_id integer NOT NULL,
    product_id integer NOT NULL,
    quantity integer NOT NULL,
    sale_price numeric(15,2) NOT NULL
);


ALTER TABLE public.quote_products OWNER TO douglas;

--
-- Name: quotes; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.quotes (
    id integer NOT NULL,
    additional_data text,
    status public.quote_status NOT NULL,
    discount numeric NOT NULL,
    total_amount numeric(20,2) NOT NULL,
    expiration_date timestamp with time zone NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    purchase_request_id integer NOT NULL,
    seller_id integer NOT NULL,
    reason character varying(255)
);


ALTER TABLE public.quotes OWNER TO douglas;

--
-- Name: quotes_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.quotes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.quotes_id_seq OWNER TO douglas;

--
-- Name: quotes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.quotes_id_seq OWNED BY public.quotes.id;


--
-- Name: sellers; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.sellers (
    person_id integer NOT NULL,
    positive_sales_count integer DEFAULT 0 NOT NULL,
    negative_sales_count integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone
);


ALTER TABLE public.sellers OWNER TO douglas;

--
-- Name: sellers_person_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.sellers_person_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sellers_person_id_seq OWNER TO douglas;

--
-- Name: sellers_person_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.sellers_person_id_seq OWNED BY public.sellers.person_id;


--
-- Name: shipments; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.shipments (
    id integer NOT NULL,
    cost numeric(10,2) NOT NULL,
    status public.shipment_status NOT NULL,
    method public.shipment_method NOT NULL,
    estimated_time date,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    receiver_address_id integer,
    quote_id integer NOT NULL
);


ALTER TABLE public.shipments OWNER TO douglas;

--
-- Name: shipments_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.shipments_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.shipments_id_seq OWNER TO douglas;

--
-- Name: shipments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.shipments_id_seq OWNED BY public.shipments.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.users (
    id integer NOT NULL,
    email character varying(255) NOT NULL,
    username character varying(25) NOT NULL,
    display_name character varying(255) NOT NULL,
    password character varying(100) NOT NULL,
    role public.users_role NOT NULL,
    status public.status_entity NOT NULL,
    password_reset_token character varying(255),
    password_expires_in timestamp with time zone,
    last_active timestamp with time zone,
    last_inactive timestamp with time zone,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    status_changed_by integer,
    avatar_id integer
);


ALTER TABLE public.users OWNER TO douglas;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO douglas;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: addresses id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.addresses ALTER COLUMN id SET DEFAULT nextval('public.addresses_id_seq'::regclass);


--
-- Name: buyers person_id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.buyers ALTER COLUMN person_id SET DEFAULT nextval('public.buyers_person_id_seq'::regclass);


--
-- Name: categories id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.categories ALTER COLUMN id SET DEFAULT nextval('public.categories_id_seq'::regclass);


--
-- Name: files id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.files ALTER COLUMN id SET DEFAULT nextval('public.files_id_seq'::regclass);


--
-- Name: notifications id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.notifications ALTER COLUMN id SET DEFAULT nextval('public.notifications_id_seq'::regclass);


--
-- Name: orders id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.orders ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);


--
-- Name: people id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.people ALTER COLUMN id SET DEFAULT nextval('public.people_id_seq'::regclass);


--
-- Name: product_items id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.product_items ALTER COLUMN id SET DEFAULT nextval('public.product_items_id_seq'::regclass);


--
-- Name: products id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.products ALTER COLUMN id SET DEFAULT nextval('public.products_id_seq'::regclass);


--
-- Name: purchase_requests id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.purchase_requests ALTER COLUMN id SET DEFAULT nextval('public.purchase_requests_id_seq'::regclass);


--
-- Name: quotes id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.quotes ALTER COLUMN id SET DEFAULT nextval('public.quotes_id_seq'::regclass);


--
-- Name: sellers person_id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.sellers ALTER COLUMN person_id SET DEFAULT nextval('public.sellers_person_id_seq'::regclass);


--
-- Name: shipments id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.shipments ALTER COLUMN id SET DEFAULT nextval('public.shipments_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: addresses; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.addresses (id, postal_code, street, district, city, province_code, country_name, building_number, additional_data, created_at, updated_at, person_id) FROM stdin;
1	08730700	Rua José Cury Andere	Alto Ipiranga	Mogi das Cruzes	SP	Brasil	637	\N	2019-01-06 12:21:03.106418+00	\N	4
2	08430400	Rua Manuel da Silva Leão	Jardim Centenário	São Paulo	SP	Brasil	451	\N	2019-05-06 23:05:12.567818+00	\N	8
3	08663120	Rua Júlio Calamari	Jardim Casa Branca	Suzano	SP	Brasil	45	\N	2019-05-11 14:28:47.100598+00	\N	3
4	08730700	Rua José Cury Andere	Alto Ipiranga	Mogi das Cruzes	SP	Brasil	633	\N	2019-05-11 15:49:17.078737+00	\N	9
\.


--
-- Data for Name: buyers; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.buyers (person_id, created_at) FROM stdin;
1	2018-12-30 17:55:12.512843+00
2	2019-01-05 16:43:25.551812+00
3	2019-01-05 18:25:57.339341+00
4	2019-01-06 11:42:08.015613+00
5	2019-01-06 18:14:31.658472+00
6	2019-01-06 18:17:12.299834+00
7	2019-01-06 18:19:13.975749+00
8	2019-05-06 22:58:27.552498+00
9	2019-05-11 15:48:45.277936+00
\.


--
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.categories (id, title, is_last_child, layer, description, status, created_at, updated_at, parent_id) FROM stdin;
2	Consoles	t	2	\N	ACTIVE	2019-01-05 16:51:14.078027+00	\N	1
3	Jogos	t	2	\N	ACTIVE	2019-01-05 16:51:21.888254+00	\N	1
4	PC Gaming	t	2	\N	ACTIVE	2019-01-05 16:51:28.94234+00	\N	1
5	Acessórios e outros	t	2	\N	ACTIVE	2019-01-05 16:51:35.490241+00	\N	1
1	Games	f	1	\N	ACTIVE	2019-01-05 16:51:02.802758+00	\N	\N
7	Desktops	t	2	\N	ACTIVE	2019-01-05 16:52:05.085608+00	\N	6
8	Notebooks	t	2	\N	ACTIVE	2019-01-05 16:52:16.026997+00	\N	6
6	Eletrônicos	f	1	\N	ACTIVE	2019-01-05 16:51:45.968933+00	\N	\N
10	Smartphones	t	2	\N	ACTIVE	2019-01-06 18:43:35.293832+00	\N	9
9	Celulares e Telefonia	f	1	\N	ACTIVE	2019-01-06 18:43:15.811662+00	\N	\N
\.


--
-- Data for Name: files; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.files (id, file_path, name, size, type, url_path, created_at, updated_at, subtype) FROM stdin;
23	product_pictures/universogamer/1557585200676_playstation-4-pro-box.jpg	playstation-4-pro-box.jpg	130067	image	https://smartsearch-app.s3.amazonaws.com/product_pictures/universogamer/1557585200676_playstation-4-pro-box.jpg	2019-05-11 14:33:24.906+00	\N	jpeg
24	product_pictures/universogamer/1557586457049_xbox-one.jpeg	xbox-one.jpeg	19671	image	https://smartsearch-app.s3.amazonaws.com/product_pictures/universogamer/1557586457049_xbox-one.jpeg	2019-05-11 14:54:20.099+00	\N	jpeg
25	product_pictures/gamespot/1557586696251_playstation-4.jpeg	playstation-4.jpeg	97329	image	https://smartsearch-app.s3.amazonaws.com/product_pictures/gamespot/1557586696251_playstation-4.jpeg	2019-05-11 14:58:17.972+00	\N	jpeg
26	product_pictures/gamespot/1557586950996_Xbox-One-X-1TB-Black-Edition.jpg	Xbox-One-X-1TB-Black-Edition.jpg	25111	image	https://smartsearch-app.s3.amazonaws.com/product_pictures/gamespot/1557586950996_Xbox-One-X-1TB-Black-Edition.jpg	2019-05-11 15:02:32.488+00	\N	jpeg
27	product_pictures/douglas18/1557589819829_iphone-xs-1.jpg	iphone-xs-1.jpg	46647	image	https://smartsearch-app.s3.amazonaws.com/product_pictures/douglas18/1557589819829_iphone-xs-1.jpg	2019-05-11 15:50:22.354+00	\N	jpeg
\.


--
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.notifications (id, resource_type, status, resource_id, content, created_at, updated_at, from_user_id, to_user_id) FROM stdin;
72	PURCHASE_REQUEST	PENDING	10034	Novo pedido de compra encontrado para você no valor de R$ 17.234,70	2019-06-09 18:57:36.238+00	2019-06-09 18:57:36.238+00	\N	9
71	PURCHASE_REQUEST	VIEWED	10034	Novo pedido de compra encontrado para você no valor de R$ 17.234,70	2019-06-09 18:57:33.801+00	2019-06-09 18:57:33.801+00	\N	7
73	QUOTE	VIEWED	18	[PEDIDO Nº10034]: Cotação de R$ 16.501,21 recebida	2019-06-09 19:09:49.713+00	2019-06-09 19:09:49.713+00	7	4
\.


--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.orders (id, status, total_amount, created_at, updated_at, quote_id, shipment_id) FROM stdin;
\.


--
-- Data for Name: people; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.people (id, account_owner, tel, cnpj, corporate_name, state_registration, created_at, updated_at, user_id) FROM stdin;
1	Fabrício Ferdinando	1145762541	65168434316046	Gamespot LTDA	241530561614632	2018-12-30 17:55:12.512843+00	\N	1
2	Douglas Vinícius Cassimiro de Lira	1147964751	10148160840680	Smartsearch	146406166	2019-01-05 16:43:25.551812+00	\N	2
4	Victor Souza	1146845651	16041604680141	Game Show	1654840125310	2019-01-06 11:42:08.015613+00	2019-01-06 12:21:03.106418+00	4
5	Gabriel Alves	1147966664	46016346014686	High Tech	4106840698460160	2019-01-06 18:14:31.658472+00	\N	5
6	Bruno Sampaio Alvarenga	1446987911	16010006540146	Informa Tech LTDA	42152041561685	2019-01-06 18:17:12.299834+00	\N	6
7	Cristina Marques Olimpia	1546849111	61014968090184	Eletronic Volt LTDA	5106806646165610	2019-01-06 18:19:13.975749+00	\N	7
8	Rodolfo Sano	1147224244	15616500000165	Games 4Day	5601321	2019-05-06 22:58:27.552498+00	2019-05-06 23:05:12.567818+00	8
3	Amanda Rodrigues	1147653546	11680460168610	Universo Gamer	54505154161507470	2019-01-05 18:25:57.339341+00	2019-05-11 14:28:47.100598+00	3
9	Douglas Lira	1146587711	11325522000116	Douglas Industries LTDA	19982316	2019-05-11 15:48:45.277936+00	2019-05-11 15:49:17.078737+00	9
\.


--
-- Data for Name: pr_products; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.pr_products (purchase_request_id, product_item_id, quantity, additional_spec) FROM stdin;
10034	22	3	\N
\.


--
-- Data for Name: product_galleries; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.product_galleries (product_id, picture_id) FROM stdin;
27	23
28	24
29	25
30	26
31	27
\.


--
-- Data for Name: product_item_galleries; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.product_item_galleries (product_item_id, picture_id) FROM stdin;
20	23
21	24
20	25
21	26
22	27
\.


--
-- Data for Name: product_items; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.product_items (id, title, views_count, relevance, base_price, max_price, min_price, status, created_at, updated_at) FROM stdin;
20	Playstation 4 Pro	0	2	2774.95	2849.90	2699.99	ACTIVE	2019-05-11 14:33:24.825+00	2019-05-11 14:58:17.969+00
21	Xbox One X	0	2	2777.02	2799.99	2754.05	ACTIVE	2019-05-11 14:54:19.95+00	2019-05-11 15:02:32.486+00
22	Iphone XS	2	2	5744.90	5789.90	5699.90	ACTIVE	2019-05-11 15:50:22.314+00	2019-06-09 18:57:02.29+00
\.


--
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.products (id, title, description, base_price, sold_quantity, available_quantity, status, situation, created_at, updated_at, seller_id, category_id, product_item_id) FROM stdin;
27	Playstation 4 Pro		2699.99	0	7	ACTIVE	LINKED	2019-05-11 14:33:24.825+00	\N	3	2	20
28	Xbox One X		2799.99	0	8	ACTIVE	LINKED	2019-05-11 14:54:19.95+00	\N	3	2	21
29	Playstation 4 Pro		2849.90	0	12	ACTIVE	LINKED	2019-05-11 14:58:17.963+00	\N	1	2	20
30	Xbox One X	\N	2754.05	0	7	ACTIVE	LINKED	2019-05-11 15:02:32.484+00	\N	1	2	21
31	Iphone XS		5699.90	0	9	ACTIVE	LINKED	2019-05-11 15:50:22.314+00	\N	9	2	22
32	Iphone XS		5789.90	0	4	ACTIVE	LINKED	2019-06-09 18:55:32.877+00	\N	7	10	22
\.


--
-- Data for Name: purchase_requests; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.purchase_requests (id, additional_data, stage, due_date, total_amount, views_count, propagation_count, quotes_visibility, closed_at, created_at, updated_at, buyer_id) FROM stdin;
10034		UNDER_QUOTATION	2019-06-13 03:00:00+00	17234.70000	9	2	t	\N	2019-06-09 18:57:03.006+00	2019-06-09 18:57:28.823+00	4
\.


--
-- Data for Name: quote_products; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.quote_products (quote_id, product_id, quantity, sale_price) FROM stdin;
18	32	3	5789.90
\.


--
-- Data for Name: quotes; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.quotes (id, additional_data, status, discount, total_amount, expiration_date, created_at, updated_at, purchase_request_id, seller_id, reason) FROM stdin;
18	\N	UNDER_REVIEW	5	16501.22	2019-06-12 03:00:00+00	2019-06-09 19:09:49.208+00	\N	10034	7	\N
\.


--
-- Data for Name: sellers; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.sellers (person_id, positive_sales_count, negative_sales_count, created_at) FROM stdin;
1	0	0	2018-12-30 17:55:12.512843+00
2	0	0	2019-01-05 16:43:25.551812+00
3	0	0	2019-01-05 18:25:57.339341+00
4	0	0	2019-01-06 11:42:08.015613+00
5	0	0	2019-01-06 18:14:31.658472+00
6	0	0	2019-01-06 18:17:12.299834+00
7	0	0	2019-01-06 18:19:13.975749+00
8	0	0	2019-05-06 22:58:27.552498+00
9	0	0	2019-05-11 15:48:45.277936+00
\.


--
-- Data for Name: shipments; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.shipments (id, cost, status, method, estimated_time, created_at, updated_at, receiver_address_id, quote_id) FROM stdin;
24	0.00	HANDLING	FREE	2019-06-21	2019-06-09 19:09:49.368+00	\N	1	18
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.users (id, email, username, display_name, password, role, status, password_reset_token, password_expires_in, last_active, last_inactive, created_at, updated_at, status_changed_by, avatar_id) FROM stdin;
2	douglas.vclira@gmail.com	douglira	Douglas Lira	$2a$10$woKhqmnd2ZIyjEBeEeKPEuT3MxCZRxh07vFqFQqCUp06O8gEEH2v6	ADMIN	ACTIVE	\N	\N	\N	\N	2019-01-05 16:43:25.551812+00	\N	\N	\N
1	gamespot@contato.com.br	gamespot	Fabrício Ferdinando	$2a$10$ChVC8IcquFqmX78hbBIGYuVSmfUdZvHzwqzDvpvTul1nK0/XfXm6q	COMMON	ACTIVE	\N	\N	\N	\N	2018-12-30 17:55:12.512843+00	\N	\N	\N
4	victor.souza@hotmail.com	victorsouza	Victor Souza	$2a$10$qakYBknmhvTunrYCBSSNOunS5dX5cNsDgIR2a2nDUyjHqFEmFKnCe	COMMON	ACTIVE	\N	\N	\N	\N	2019-01-06 11:42:08.015613+00	2019-01-06 12:21:03.106418+00	\N	\N
5	hightech@contato.com	hightech	Gabriel Alves	$2a$10$SdRE8U.8bVx56nODMBJc3.L91PO1DzORjkz4OzkVrGn1myNQDYi9.	COMMON	ACTIVE	\N	\N	\N	\N	2019-01-06 18:14:31.658472+00	\N	\N	\N
6	informatech@contato.com	informatech	Bruno Alvarenga	$2a$10$4cFAEyvJLVVxMblFHkVZGOB3Y1GwDPlN2qmruHOU0zF0lDWHeAfYa	COMMON	ACTIVE	\N	\N	\N	\N	2019-01-06 18:17:12.299834+00	\N	\N	\N
7	eletrovolt@contato.com	eletrovolt	Cristina Olimpia	$2a$10$ZPJOG4x5XZB/2EfuueKudu2JMqgelAj7zO/h6wCCVZsNlS/9rTqNC	COMMON	ACTIVE	\N	\N	\N	\N	2019-01-06 18:19:13.975749+00	\N	\N	\N
8	rodolfo@xbt.com.br	rodoxbt	Rodolfo Sano	$2a$10$twEDl6hjdkJjPcmbCT5F0OUTOmW4Lq29Glp3gfhYU2FkmXIz7Xcwu	COMMON	ACTIVE	\N	\N	\N	\N	2019-05-06 22:58:27.552498+00	2019-05-06 23:05:12.567818+00	\N	\N
3	contato@universogamer.com.br	universogamer	Amanda Rodrigues	$2a$10$iP2F4sGA3SKDaCnaSkHjJuYPqvLmoNrgodWYxPaJHAvBB197X3jLq	COMMON	ACTIVE	\N	\N	\N	\N	2019-01-05 18:25:57.339341+00	2019-05-11 14:28:47.100598+00	\N	\N
9	douglasvinicius.clira@hotmail.com	douglas18	Douglas Lira	$2a$10$Zh9aND1Q6yj3WdT05HWHjOYBoO.i72J.6hhOw0jhLAcwpJZtZIa7y	COMMON	ACTIVE	\N	\N	\N	\N	2019-05-11 15:48:45.277936+00	2019-05-11 15:49:17.078737+00	\N	\N
\.


--
-- Name: addresses_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.addresses_id_seq', 4, true);


--
-- Name: buyers_person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.buyers_person_id_seq', 1, false);


--
-- Name: categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.categories_id_seq', 10, true);


--
-- Name: files_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.files_id_seq', 27, true);


--
-- Name: notifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.notifications_id_seq', 73, true);


--
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.orders_id_seq', 9, true);


--
-- Name: people_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.people_id_seq', 9, true);


--
-- Name: pr_sequence; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.pr_sequence', 10034, true);


--
-- Name: product_items_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.product_items_id_seq', 22, true);


--
-- Name: products_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.products_id_seq', 32, true);


--
-- Name: purchase_requests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.purchase_requests_id_seq', 1, false);


--
-- Name: quotes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.quotes_id_seq', 18, true);


--
-- Name: sellers_person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.sellers_person_id_seq', 1, false);


--
-- Name: shipments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.shipments_id_seq', 24, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.users_id_seq', 9, true);


--
-- Name: addresses addresses_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.addresses
    ADD CONSTRAINT addresses_pkey PRIMARY KEY (id);


--
-- Name: buyers buyers_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.buyers
    ADD CONSTRAINT buyers_pkey PRIMARY KEY (person_id);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- Name: files files_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.files
    ADD CONSTRAINT files_pkey PRIMARY KEY (id);


--
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- Name: people people_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.people
    ADD CONSTRAINT people_pkey PRIMARY KEY (id);


--
-- Name: pr_products pr_products_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.pr_products
    ADD CONSTRAINT pr_products_pkey PRIMARY KEY (purchase_request_id, product_item_id);


--
-- Name: product_galleries product_galleries_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.product_galleries
    ADD CONSTRAINT product_galleries_pkey PRIMARY KEY (product_id, picture_id);


--
-- Name: product_item_galleries product_item_galleries_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.product_item_galleries
    ADD CONSTRAINT product_item_galleries_pkey PRIMARY KEY (product_item_id, picture_id);


--
-- Name: product_items product_items_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.product_items
    ADD CONSTRAINT product_items_pkey PRIMARY KEY (id);


--
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- Name: purchase_requests purchase_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.purchase_requests
    ADD CONSTRAINT purchase_requests_pkey PRIMARY KEY (id);


--
-- Name: quote_products quote_products_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.quote_products
    ADD CONSTRAINT quote_products_pkey PRIMARY KEY (quote_id, product_id);


--
-- Name: quotes quotes_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.quotes
    ADD CONSTRAINT quotes_pkey PRIMARY KEY (id);


--
-- Name: sellers sellers_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.sellers
    ADD CONSTRAINT sellers_pkey PRIMARY KEY (person_id);


--
-- Name: shipments shipments_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.shipments
    ADD CONSTRAINT shipments_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: notifications_created_at; Type: INDEX; Schema: public; Owner: douglas
--

CREATE INDEX notifications_created_at ON public.notifications USING btree (created_at);


--
-- Name: notifications_from_to_users; Type: INDEX; Schema: public; Owner: douglas
--

CREATE INDEX notifications_from_to_users ON public.notifications USING btree (from_user_id, to_user_id);


--
-- Name: notifications_from_users; Type: INDEX; Schema: public; Owner: douglas
--

CREATE INDEX notifications_from_users ON public.notifications USING btree (from_user_id);


--
-- Name: notifications_status_to_users; Type: INDEX; Schema: public; Owner: douglas
--

CREATE INDEX notifications_status_to_users ON public.notifications USING btree (status, to_user_id);


--
-- Name: notifications_to_users; Type: INDEX; Schema: public; Owner: douglas
--

CREATE INDEX notifications_to_users ON public.notifications USING btree (to_user_id);


--
-- Name: pr_products_pkey_pi; Type: INDEX; Schema: public; Owner: douglas
--

CREATE INDEX pr_products_pkey_pi ON public.pr_products USING btree (product_item_id);


--
-- Name: pr_products_pkey_pr; Type: INDEX; Schema: public; Owner: douglas
--

CREATE INDEX pr_products_pkey_pr ON public.pr_products USING btree (purchase_request_id);


--
-- Name: pr_products_pkeys; Type: INDEX; Schema: public; Owner: douglas
--

CREATE INDEX pr_products_pkeys ON public.pr_products USING btree (product_item_id, purchase_request_id);


--
-- Name: purchase_requests_buyer_stage; Type: INDEX; Schema: public; Owner: douglas
--

CREATE INDEX purchase_requests_buyer_stage ON public.purchase_requests USING btree (buyer_id, stage);


--
-- Name: quote_id_pr_seller; Type: INDEX; Schema: public; Owner: douglas
--

CREATE INDEX quote_id_pr_seller ON public.quotes USING btree (id, purchase_request_id, seller_id);


--
-- Name: quote_pr; Type: INDEX; Schema: public; Owner: douglas
--

CREATE INDEX quote_pr ON public.quotes USING btree (purchase_request_id);


--
-- Name: quote_pr_seller; Type: INDEX; Schema: public; Owner: douglas
--

CREATE INDEX quote_pr_seller ON public.quotes USING btree (purchase_request_id, seller_id);


--
-- Name: quote_products_index; Type: INDEX; Schema: public; Owner: douglas
--

CREATE INDEX quote_products_index ON public.quote_products USING btree (quote_id, product_id);


--
-- Name: addresses addresses_person_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.addresses
    ADD CONSTRAINT addresses_person_id_fkey FOREIGN KEY (person_id) REFERENCES public.people(id);


--
-- Name: buyers buyers_person_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.buyers
    ADD CONSTRAINT buyers_person_id_fkey FOREIGN KEY (person_id) REFERENCES public.people(id);


--
-- Name: categories categories_parent_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES public.categories(id);


--
-- Name: notifications notifications_from_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_from_user_id_fkey FOREIGN KEY (from_user_id) REFERENCES public.users(id);


--
-- Name: notifications notifications_to_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_to_user_id_fkey FOREIGN KEY (to_user_id) REFERENCES public.users(id);


--
-- Name: orders orders_quote_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_quote_id_fkey FOREIGN KEY (quote_id) REFERENCES public.quotes(id);


--
-- Name: orders orders_shipment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_shipment_id_fkey FOREIGN KEY (shipment_id) REFERENCES public.shipments(id);


--
-- Name: people people_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.people
    ADD CONSTRAINT people_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: pr_products pr_products_product_item_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.pr_products
    ADD CONSTRAINT pr_products_product_item_id_fkey FOREIGN KEY (product_item_id) REFERENCES public.product_items(id);


--
-- Name: pr_products pr_products_purchase_request_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.pr_products
    ADD CONSTRAINT pr_products_purchase_request_id_fkey FOREIGN KEY (purchase_request_id) REFERENCES public.purchase_requests(id);


--
-- Name: product_galleries product_galleries_picture_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.product_galleries
    ADD CONSTRAINT product_galleries_picture_id_fkey FOREIGN KEY (picture_id) REFERENCES public.files(id);


--
-- Name: product_galleries product_galleries_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.product_galleries
    ADD CONSTRAINT product_galleries_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.products(id);


--
-- Name: product_item_galleries product_item_galleries_picture_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.product_item_galleries
    ADD CONSTRAINT product_item_galleries_picture_id_fkey FOREIGN KEY (picture_id) REFERENCES public.files(id);


--
-- Name: product_item_galleries product_item_galleries_product_item_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.product_item_galleries
    ADD CONSTRAINT product_item_galleries_product_item_id_fkey FOREIGN KEY (product_item_id) REFERENCES public.product_items(id);


--
-- Name: products products_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- Name: products products_produtct_item_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_produtct_item_id_fkey FOREIGN KEY (product_item_id) REFERENCES public.product_items(id);


--
-- Name: products products_seller_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_seller_id_fkey FOREIGN KEY (seller_id) REFERENCES public.sellers(person_id);


--
-- Name: purchase_requests purchase_requests_buyer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.purchase_requests
    ADD CONSTRAINT purchase_requests_buyer_id_fkey FOREIGN KEY (buyer_id) REFERENCES public.buyers(person_id);


--
-- Name: quote_products quote_products_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.quote_products
    ADD CONSTRAINT quote_products_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.products(id);


--
-- Name: quote_products quote_products_quote_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.quote_products
    ADD CONSTRAINT quote_products_quote_id_fkey FOREIGN KEY (quote_id) REFERENCES public.quotes(id);


--
-- Name: quotes quotes_purchase_request_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.quotes
    ADD CONSTRAINT quotes_purchase_request_id_fkey FOREIGN KEY (purchase_request_id) REFERENCES public.purchase_requests(id);


--
-- Name: quotes quotes_seller_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.quotes
    ADD CONSTRAINT quotes_seller_id_fkey FOREIGN KEY (seller_id) REFERENCES public.sellers(person_id);


--
-- Name: sellers sellers_person_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.sellers
    ADD CONSTRAINT sellers_person_id_fkey FOREIGN KEY (person_id) REFERENCES public.people(id);


--
-- Name: shipments shipments_quote_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.shipments
    ADD CONSTRAINT shipments_quote_id_fkey FOREIGN KEY (quote_id) REFERENCES public.quotes(id);


--
-- Name: shipments shipments_receiver_address_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.shipments
    ADD CONSTRAINT shipments_receiver_address_id_fkey FOREIGN KEY (receiver_address_id) REFERENCES public.addresses(id);


--
-- Name: users users_avatar_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_avatar_id_fkey FOREIGN KEY (avatar_id) REFERENCES public.files(id);


--
-- Name: users users_status_changed_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_status_changed_by_fkey FOREIGN KEY (status_changed_by) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--

--
-- PostgreSQL database dump
--

-- Dumped from database version 11.1 (Debian 11.1-1.pgdg90+1)
-- Dumped by pg_dump version 11.1 (Debian 11.1-1.pgdg90+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: v2saude; Type: DATABASE; Schema: -; Owner: douglas
--

CREATE DATABASE v2saude WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8';


ALTER DATABASE v2saude OWNER TO douglas;

\connect v2saude

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: SequelizeMeta; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public."SequelizeMeta" (
    name character varying(255) NOT NULL
);


ALTER TABLE public."SequelizeMeta" OWNER TO douglas;

--
-- Name: permissoes; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.permissoes (
    id integer NOT NULL,
    nome character varying(255) NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone
);


ALTER TABLE public.permissoes OWNER TO douglas;

--
-- Name: permissoes_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.permissoes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.permissoes_id_seq OWNER TO douglas;

--
-- Name: permissoes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.permissoes_id_seq OWNED BY public.permissoes.id;


--
-- Name: usuarios; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.usuarios (
    id integer NOT NULL,
    nome character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    hash_senha character varying(255) NOT NULL,
    admin boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);


ALTER TABLE public.usuarios OWNER TO douglas;

--
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.usuarios_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.usuarios_id_seq OWNER TO douglas;

--
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.usuarios_id_seq OWNED BY public.usuarios.id;


--
-- Name: usuarios_permissoes; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.usuarios_permissoes (
    id integer NOT NULL,
    usuario_id integer,
    permissao_id integer
);


ALTER TABLE public.usuarios_permissoes OWNER TO douglas;

--
-- Name: usuarios_permissoes_id_seq; Type: SEQUENCE; Schema: public; Owner: douglas
--

CREATE SEQUENCE public.usuarios_permissoes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.usuarios_permissoes_id_seq OWNER TO douglas;

--
-- Name: usuarios_permissoes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: douglas
--

ALTER SEQUENCE public.usuarios_permissoes_id_seq OWNED BY public.usuarios_permissoes.id;


--
-- Name: permissoes id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.permissoes ALTER COLUMN id SET DEFAULT nextval('public.permissoes_id_seq'::regclass);


--
-- Name: usuarios id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq'::regclass);


--
-- Name: usuarios_permissoes id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.usuarios_permissoes ALTER COLUMN id SET DEFAULT nextval('public.usuarios_permissoes_id_seq'::regclass);


--
-- Data for Name: SequelizeMeta; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public."SequelizeMeta" (name) FROM stdin;
20190403014309-create-users.js
20190404000616-create-permissions.js
20190404002426-add-relation-users-permissions.js
\.


--
-- Data for Name: permissoes; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.permissoes (id, nome, created_at, updated_at) FROM stdin;
1	ADMINISTRADOR_GLOBAL	\N	\N
2	ADMINISTRADOR	\N	\N
3	USUARIO	\N	\N
\.


--
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.usuarios (id, nome, email, hash_senha, admin, created_at, updated_at) FROM stdin;
16	Douglas Lira	douglas@xbt.com.br	$2a$10$6HTEJqw4KrV2HQGBwrWx.OBSovxiK6IxWz7lPD1L8I4NTMmQnHcl2	f	2019-04-04 01:58:54.981+00	2019-04-04 01:58:54.981+00
\.


--
-- Data for Name: usuarios_permissoes; Type: TABLE DATA; Schema: public; Owner: douglas
--

COPY public.usuarios_permissoes (id, usuario_id, permissao_id) FROM stdin;
19	16	1
20	16	2
21	16	3
\.


--
-- Name: permissoes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.permissoes_id_seq', 3, true);


--
-- Name: usuarios_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.usuarios_id_seq', 16, true);


--
-- Name: usuarios_permissoes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: douglas
--

SELECT pg_catalog.setval('public.usuarios_permissoes_id_seq', 21, true);


--
-- Name: SequelizeMeta SequelizeMeta_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public."SequelizeMeta"
    ADD CONSTRAINT "SequelizeMeta_pkey" PRIMARY KEY (name);


--
-- Name: permissoes permissoes_nome_key; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.permissoes
    ADD CONSTRAINT permissoes_nome_key UNIQUE (nome);


--
-- Name: permissoes permissoes_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.permissoes
    ADD CONSTRAINT permissoes_pkey PRIMARY KEY (id);


--
-- Name: usuarios usuarios_email_key; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_email_key UNIQUE (email);


--
-- Name: usuarios_permissoes usuarios_permissoes_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.usuarios_permissoes
    ADD CONSTRAINT usuarios_permissoes_pkey PRIMARY KEY (id);


--
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- Name: usuarios_permissoes usuarios_permissoes_permissao_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.usuarios_permissoes
    ADD CONSTRAINT usuarios_permissoes_permissao_id_fkey FOREIGN KEY (permissao_id) REFERENCES public.permissoes(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: usuarios_permissoes usuarios_permissoes_usuario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.usuarios_permissoes
    ADD CONSTRAINT usuarios_permissoes_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

--
-- PostgreSQL database cluster dump complete
--

