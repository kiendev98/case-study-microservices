CREATE TABLE IF NOT EXISTS public.organization
(
    organization_id bigserial,
    name text COLLATE pg_catalog."default",
    contact_name text COLLATE pg_catalog."default",
    contact_email text COLLATE pg_catalog."default",
    contact_phone text COLLATE pg_catalog."default",
    CONSTRAINT organization_pkey PRIMARY KEY (organization_id)
)

TABLESPACE pg_default;

ALTER TABLE public.organization
    OWNER to ostock;


CREATE TABLE IF NOT EXISTS public.license
(
    license_id bigserial,
    organization_id bigint,
    description text COLLATE pg_catalog."default",
    product_name text COLLATE pg_catalog."default" NOT NULL,
    license_type text COLLATE pg_catalog."default" NOT NULL,
    comment text COLLATE pg_catalog."default",
    CONSTRAINT license_pkey PRIMARY KEY (license_id),
    CONSTRAINT license_organization_id_fkey FOREIGN KEY (organization_id)
        REFERENCES public.organization (organization_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

TABLESPACE pg_default;

ALTER TABLE public.license
    OWNER to ostock;