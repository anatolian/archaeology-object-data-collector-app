--
-- PostgreSQL database dump
--

-- Dumped from database version 9.1.3
-- Dumped by pg_dump version 9.1.3

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 1893 (class 1262 OID 30103)
-- Name: excavation; Type: DATABASE; Schema: -; Owner: -
--

CREATE DATABASE excavation WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'English_United States.1252' LC_CTYPE = 'English_United States.1252';


\connect excavation

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 7 (class 2615 OID 30116)
-- Name: excavation; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA excavation;


--
-- TOC entry 6 (class 2615 OID 30104)
-- Name: options; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA options;


--
-- TOC entry 8 (class 2615 OID 30142)
-- Name: samples; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA samples;


--
-- TOC entry 9 (class 2615 OID 30110)
-- Name: sde; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA sde;


--
-- TOC entry 170 (class 3079 OID 11639)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 1894 (class 0 OID 0)
-- Dependencies: 170
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = excavation, pg_catalog;

SET default_with_oids = false;

--
-- TOC entry 166 (class 1259 OID 30117)
-- Dependencies: 7
-- Name: contexts_spatial; Type: TABLE; Schema: excavation; Owner: -
--

CREATE TABLE contexts_spatial (
    area_easting integer NOT NULL,
    area_northing integer NOT NULL,
    context_number integer NOT NULL,
    open_date date NOT NULL,
    close_date date
);


--
-- TOC entry 167 (class 1259 OID 30127)
-- Dependencies: 7
-- Name: contexts_spatial_photographs; Type: TABLE; Schema: excavation; Owner: -
--

CREATE TABLE contexts_spatial_photographs (
    area_easting integer NOT NULL,
    area_northing integer NOT NULL,
    photograph_date date NOT NULL,
    photograph_number integer NOT NULL,
    context_number integer NOT NULL
);


SET search_path = options, pg_catalog;

--
-- TOC entry 169 (class 1259 OID 30153)
-- Dependencies: 6
-- Name: photograph_file_types; Type: TABLE; Schema: options; Owner: -
--

CREATE TABLE photograph_file_types (
    file_types character varying(5) NOT NULL
);


--
-- TOC entry 164 (class 1259 OID 30105)
-- Dependencies: 6
-- Name: procedure_properties; Type: TABLE; Schema: options; Owner: -
--

CREATE TABLE procedure_properties (
    property character varying(50) NOT NULL,
    property_value character varying(150) NOT NULL
);


SET search_path = samples, pg_catalog;

--
-- TOC entry 168 (class 1259 OID 30143)
-- Dependencies: 8
-- Name: samples; Type: TABLE; Schema: samples; Owner: -
--

CREATE TABLE samples (
    area_easting integer NOT NULL,
    area_northing integer NOT NULL,
    context_number integer NOT NULL,
    sample_number integer NOT NULL,
    material character varying(25)
);


SET search_path = sde, pg_catalog;

--
-- TOC entry 165 (class 1259 OID 30111)
-- Dependencies: 9
-- Name: excavation_areas; Type: TABLE; Schema: sde; Owner: -
--

CREATE TABLE excavation_areas (
    area_easting integer NOT NULL,
    area_northing integer NOT NULL
);


SET search_path = excavation, pg_catalog;

--
-- TOC entry 1887 (class 0 OID 30117)
-- Dependencies: 166
-- Data for Name: contexts_spatial; Type: TABLE DATA; Schema: excavation; Owner: -
--

COPY contexts_spatial (area_easting, area_northing, context_number, open_date, close_date) FROM stdin;
916	941	1	2014-06-01	\N
916	941	2	2014-06-02	\N
916	941	3	2014-06-02	\N
916	940	1	2014-06-01	\N
916	940	2	2014-06-01	\N
913	907	1	2014-06-02	\N
\.


--
-- TOC entry 1888 (class 0 OID 30127)
-- Dependencies: 167
-- Data for Name: contexts_spatial_photographs; Type: TABLE DATA; Schema: excavation; Owner: -
--

COPY contexts_spatial_photographs (area_easting, area_northing, photograph_date, photograph_number, context_number) FROM stdin;
916	941	2014-06-01	1	1
\.


SET search_path = options, pg_catalog;

--
-- TOC entry 1890 (class 0 OID 30153)
-- Dependencies: 169
-- Data for Name: photograph_file_types; Type: TABLE DATA; Schema: options; Owner: -
--

COPY photograph_file_types (file_types) FROM stdin;
con
pic
fab
tsn
\.


--
-- TOC entry 1885 (class 0 OID 30105)
-- Dependencies: 164
-- Data for Name: procedure_properties; Type: TABLE DATA; Schema: options; Owner: -
--

COPY procedure_properties (property, property_value) FROM stdin;
context_subpath	context\\
3d_subpath	3d\\
sample_subpath	sample\\
sample_label_font	Times New Roman
sample_label_font_size	12
sample_label_area_divider	:
sample_label_context_divider	.
sample_label_sample_divider	:
base_image_path	c:\\images\\
sample_label_placement	bottom - center
\.


SET search_path = samples, pg_catalog;

--
-- TOC entry 1889 (class 0 OID 30143)
-- Dependencies: 168
-- Data for Name: samples; Type: TABLE DATA; Schema: samples; Owner: -
--

COPY samples (area_easting, area_northing, context_number, sample_number, material) FROM stdin;
916	940	1	1	cloth
916	940	1	2	wool
913	907	1	1	metal
\.


SET search_path = sde, pg_catalog;

--
-- TOC entry 1886 (class 0 OID 30111)
-- Dependencies: 165
-- Data for Name: excavation_areas; Type: TABLE DATA; Schema: sde; Owner: -
--

COPY excavation_areas (area_easting, area_northing) FROM stdin;
916	941
913	907
916	940
\.


SET search_path = excavation, pg_catalog;

--
-- TOC entry 1876 (class 2606 OID 30131)
-- Dependencies: 167 167 167 167 167 167
-- Name: contexts_photographs_pkey; Type: CONSTRAINT; Schema: excavation; Owner: -
--

ALTER TABLE ONLY contexts_spatial_photographs
    ADD CONSTRAINT contexts_photographs_pkey PRIMARY KEY (area_easting, area_northing, photograph_date, photograph_number, context_number);


--
-- TOC entry 1874 (class 2606 OID 30121)
-- Dependencies: 166 166 166 166
-- Name: contexts_spatial_pkey; Type: CONSTRAINT; Schema: excavation; Owner: -
--

ALTER TABLE ONLY contexts_spatial
    ADD CONSTRAINT contexts_spatial_pkey PRIMARY KEY (area_easting, area_northing, context_number);


SET search_path = options, pg_catalog;

--
-- TOC entry 1880 (class 2606 OID 30157)
-- Dependencies: 169 169
-- Name: photograph_filetypes_pkey; Type: CONSTRAINT; Schema: options; Owner: -
--

ALTER TABLE ONLY photograph_file_types
    ADD CONSTRAINT photograph_filetypes_pkey PRIMARY KEY (file_types);


--
-- TOC entry 1870 (class 2606 OID 30109)
-- Dependencies: 164 164
-- Name: procedure_properties_pkey; Type: CONSTRAINT; Schema: options; Owner: -
--

ALTER TABLE ONLY procedure_properties
    ADD CONSTRAINT procedure_properties_pkey PRIMARY KEY (property);


SET search_path = samples, pg_catalog;

--
-- TOC entry 1878 (class 2606 OID 30147)
-- Dependencies: 168 168 168 168 168
-- Name: samples_pkey; Type: CONSTRAINT; Schema: samples; Owner: -
--

ALTER TABLE ONLY samples
    ADD CONSTRAINT samples_pkey PRIMARY KEY (area_easting, area_northing, context_number, sample_number);


SET search_path = sde, pg_catalog;

--
-- TOC entry 1872 (class 2606 OID 30115)
-- Dependencies: 165 165 165
-- Name: excavation_areas_pkey; Type: CONSTRAINT; Schema: sde; Owner: -
--

ALTER TABLE ONLY excavation_areas
    ADD CONSTRAINT excavation_areas_pkey PRIMARY KEY (area_easting, area_northing);


SET search_path = excavation, pg_catalog;

--
-- TOC entry 1882 (class 2606 OID 30768)
-- Dependencies: 167 1871 165 165 167
-- Name: contexts_photographs_areas_fkey; Type: FK CONSTRAINT; Schema: excavation; Owner: -
--

ALTER TABLE ONLY contexts_spatial_photographs
    ADD CONSTRAINT contexts_photographs_areas_fkey FOREIGN KEY (area_easting, area_northing) REFERENCES sde.excavation_areas(area_easting, area_northing) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1883 (class 2606 OID 30773)
-- Dependencies: 166 167 166 167 167 1873 166
-- Name: contexts_photographs_contexts_spatial_fkey; Type: FK CONSTRAINT; Schema: excavation; Owner: -
--

ALTER TABLE ONLY contexts_spatial_photographs
    ADD CONSTRAINT contexts_photographs_contexts_spatial_fkey FOREIGN KEY (area_easting, area_northing, context_number) REFERENCES contexts_spatial(area_easting, area_northing, context_number) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1881 (class 2606 OID 30168)
-- Dependencies: 165 165 166 166 1871
-- Name: contexts_spatial_context_fkey; Type: FK CONSTRAINT; Schema: excavation; Owner: -
--

ALTER TABLE ONLY contexts_spatial
    ADD CONSTRAINT contexts_spatial_context_fkey FOREIGN KEY (area_easting, area_northing) REFERENCES sde.excavation_areas(area_easting, area_northing) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


SET search_path = samples, pg_catalog;

--
-- TOC entry 1884 (class 2606 OID 30173)
-- Dependencies: 1873 168 168 168 166 166 166
-- Name: samples_contexts_spatial_fkey; Type: FK CONSTRAINT; Schema: samples; Owner: -
--

ALTER TABLE ONLY samples
    ADD CONSTRAINT samples_contexts_spatial_fkey FOREIGN KEY (area_easting, area_northing, context_number) REFERENCES excavation.contexts_spatial(area_easting, area_northing, context_number) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


-- Completed on 2015-03-14 11:18:57

--
-- PostgreSQL database dump complete
--

