--
-- PostgreSQL database dump
--

-- Dumped from database version 10.4 (Debian 10.4-2.pgdg90+1)
-- Dumped by pg_dump version 10.4 (Debian 10.4-2.pgdg90+1)

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
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


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
  UPDATE categories SET status = categoryStatus WHERE id = categoryId RETURNING id INTO returnedId;
  
  IF returnedId > 0 THEN
    FOR rowSelect IN SELECT id FROM categories WHERE parent_id = returnedId
    LOOP
      PERFORM * FROM func_toggle_status_categories(rowSelect.id, categoryStatus);
    END LOOP;
  END IF;
END;
$_$;


ALTER FUNCTION public.func_toggle_status_categories(categoryid integer, status public.status_entity) OWNER TO douglas;

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
    province_code character varying(2) NOT NULL,
    country_name character varying(255) DEFAULT 'Brasil'::character varying NOT NULL,
    building_number integer NOT NULL,
    additional_data text,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
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
-- Name: categories; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.categories (
    id integer NOT NULL,
    title character varying(255) NOT NULL,
    is_last_child boolean NOT NULL,
    layer integer NOT NULL,
    description text,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    parent_id integer,
    status public.status_entity NOT NULL
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
-- Name: people; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.people (
    id integer NOT NULL,
    account_owner character varying(255) NOT NULL,
    tel bigint NOT NULL,
    cnpj bigint,
    corporate_name character varying(255),
    state_registration bigint,
    user_id integer NOT NULL,
    created_at timestamp without time zone,
    updated_at timestamp without time zone
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
-- Name: users; Type: TABLE; Schema: public; Owner: douglas
--

CREATE TABLE public.users (
    id integer NOT NULL,
    avatar character varying(255),
    email character varying(255) NOT NULL,
    display_name character varying(255) NOT NULL,
    password character varying(100) NOT NULL,
    password_reset_token character varying(255),
    password_expires_in timestamp with time zone,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    last_active timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    last_inactive timestamp with time zone,
    status_changed_by integer,
    username character varying(15) NOT NULL,
    role public.users_role NOT NULL,
    status public.status_entity NOT NULL
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
-- Name: categories id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.categories ALTER COLUMN id SET DEFAULT nextval('public.categories_id_seq'::regclass);


--
-- Name: people id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.people ALTER COLUMN id SET DEFAULT nextval('public.people_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: addresses addresses_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.addresses
    ADD CONSTRAINT addresses_pkey PRIMARY KEY (id);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- Name: categories categories_title_unique; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_title_unique UNIQUE (title);


--
-- Name: people people_cnpj_unique; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.people
    ADD CONSTRAINT people_cnpj_unique UNIQUE (cnpj);


--
-- Name: people people_corporate_name_unique; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.people
    ADD CONSTRAINT people_corporate_name_unique UNIQUE (corporate_name);


--
-- Name: people people_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.people
    ADD CONSTRAINT people_pkey PRIMARY KEY (id);


--
-- Name: people people_tel_unique; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.people
    ADD CONSTRAINT people_tel_unique UNIQUE (tel);


--
-- Name: users users_email_unique; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_unique UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: users users_username_unique; Type: CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_unique UNIQUE (username);


--
-- Name: addresses addresses_personid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.addresses
    ADD CONSTRAINT addresses_personid_foreign FOREIGN KEY (person_id) REFERENCES public.people(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: categories categories_parentid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_parentid_foreign FOREIGN KEY (parent_id) REFERENCES public.categories(id) ON DELETE CASCADE;


--
-- Name: people people_userid_foreign; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.people
    ADD CONSTRAINT people_userid_foreign FOREIGN KEY (user_id) REFERENCES public.users(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: users users_statuschangedby_foreign; Type: FK CONSTRAINT; Schema: public; Owner: douglas
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_statuschangedby_foreign FOREIGN KEY (status_changed_by) REFERENCES public.users(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

