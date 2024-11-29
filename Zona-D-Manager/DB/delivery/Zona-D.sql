


-- Generado por Oracle SQL Developer Data Modeler 23.1.0.087.0806
--   en:        2024-11-24 23:25:26 CST
--   sitio:      Oracle Database 12cR2
--   tipo:      Oracle Database 12cR2

alter session set "_ORACLE_SCRIPT"=true; 
CREATE USER "Zona-D2" IDENTIFIED BY "1234";


ALTER USER "Zona-D"
DEFAULT TABLESPACE "USERS"
TEMPORARY TABLESPACE "TEMP"
ACCOUNT UNLOCK ;


-- Generado por Oracle SQL Developer Data Modeler 23.1.0.087.0806
--   en:        2024-11-28 20:01:47 CST
--   sitio:      Oracle Database 12cR2
--   tipo:      Oracle Database 12cR2



-- predefined type, no DDL - MDSYS.SDO_GEOMETRY

-- predefined type, no DDL - XMLTYPE

create or replace NONEDITIONABLE PACKAGE "Zona-D".ZONA_D_PROCESS_PKG AS 
    
    /**
     * Procesa las fichas nuevas 
    **/
    procedure ACTIVE_NEW_FICHAS_PS(PIC_FICHAS VARCHAR2);

END ZONA_D_PROCESS_PKG;
/

CREATE SEQUENCE "Zona-D".contact_seq START WITH 1 INCREMENT BY 1 MAXVALUE 999999999999 MINVALUE 1;

CREATE TABLE "Zona-D".zd_contacts (
    contact_id   NUMBER(8) NOT NULL,
    vendor_id    VARCHAR2(50 CHAR) NOT NULL,
    contact_type VARCHAR2(20 CHAR),
    description  VARCHAR2(500),
    status       VARCHAR2(10 CHAR) DEFAULT 'Active'
)
LOGGING;

ALTER TABLE "Zona-D".zd_contacts
    ADD CONSTRAINT status_contact_ck CHECK ( status IN ( 'Active', 'Inactive' ) );

COMMENT ON COLUMN "Zona-D".zd_contacts.contact_id IS
    'Clave del tipo de contacto';

COMMENT ON COLUMN "Zona-D".zd_contacts.vendor_id IS
    'clave  del vendedor';

COMMENT ON COLUMN "Zona-D".zd_contacts.contact_type IS
    'Tipo de contacto
Facebok
WA
Fisico';

COMMENT ON COLUMN "Zona-D".zd_contacts.description IS
    'Descripción del tipo de contacto. Teléfono,  face, otro';

COMMENT ON COLUMN "Zona-D".zd_contacts.status IS
    'Active. Inactive';

ALTER TABLE "Zona-D".zd_contacts ADD CONSTRAINT zd_contacts_pk PRIMARY KEY ( contact_id );

CREATE TABLE "Zona-D".zd_fichas (
    name          VARCHAR2(50 CHAR) NOT NULL,
    profile       VARCHAR2(30 CHAR) NOT NULL,
    vendor_id     VARCHAR2(30 CHAR) NOT NULL,
    creation_date DATE NOT NULL,
    begin_date    DATE,
    end_date      VARCHAR2(20 BYTE),
    status        VARCHAR2(15 CHAR) DEFAULT 'NEW'
)
TABLESPACE users LOGGING NO INMEMORY;

ALTER TABLE "Zona-D".zd_fichas
    ADD CONSTRAINT status_fk CHECK ( status IN ( 'ACTIVE', 'FINISHED', 'NEW' ) );

COMMENT ON COLUMN "Zona-D".zd_fichas.name IS
    'Nombre de usuario de la ficha';

COMMENT ON COLUMN "Zona-D".zd_fichas.profile IS
    'Código de perfil';

COMMENT ON COLUMN "Zona-D".zd_fichas.vendor_id IS
    'Clave del vendedor';

COMMENT ON COLUMN "Zona-D".zd_fichas.begin_date IS
    'Fecha de inicio de utilización de la ficha';

COMMENT ON COLUMN "Zona-D".zd_fichas.end_date IS
    'Fecha de fin de utilización de la ficha';

COMMENT ON COLUMN "Zona-D".zd_fichas.status IS
    'NEW
ACTIVE
FINISHED';

CREATE UNIQUE INDEX "Zona-D".users_pk ON
    "Zona-D".zd_fichas (
        name
    ASC )
        TABLESPACE users LOGGING;

ALTER TABLE "Zona-D".zd_fichas
    ADD CONSTRAINT users_pk PRIMARY KEY ( name )
        USING INDEX "Zona-D".users_pk;

CREATE TABLE "Zona-D".zd_profiles (
    code_profile VARCHAR2(50 CHAR) NOT NULL,
    profile_name VARCHAR2(100 CHAR),
    profile_time NUMBER,
    status       VARCHAR2(10 CHAR) DEFAULT 'Active'
)
LOGGING;

ALTER TABLE "Zona-D".zd_profiles
    ADD CONSTRAINT status_profile_ck CHECK ( status IN ( 'Active', 'Inactive' ) );

COMMENT ON COLUMN "Zona-D".zd_profiles.code_profile IS
    'Código del perfil';

COMMENT ON COLUMN "Zona-D".zd_profiles.profile_name IS
    'Nombre del perfil';

COMMENT ON COLUMN "Zona-D".zd_profiles.profile_time IS
    'Tiempo por perfil, Ejemplo:

Número de días';

COMMENT ON COLUMN "Zona-D".zd_profiles.status IS
    'Status del perfil

Active
Inactive';

ALTER TABLE "Zona-D".zd_profiles ADD CONSTRAINT zd_profiles_pk PRIMARY KEY ( code_profile );

CREATE TABLE "Zona-D".zd_vendor (
    vendor_id   VARCHAR2(30 CHAR) NOT NULL,
    vendor_name VARCHAR2(100 CHAR),
    location    VARCHAR2(500 CHAR),
    status      VARCHAR2(10 CHAR) DEFAULT 'Active'
)
LOGGING;

ALTER TABLE "Zona-D".zd_vendor
    ADD CONSTRAINT status_vendor CHECK ( status IN ( 'Active', 'Inactive' ) );

COMMENT ON COLUMN "Zona-D".zd_vendor.vendor_id IS
    'Clave de punto de venta';

COMMENT ON COLUMN "Zona-D".zd_vendor.vendor_name IS
    'Nombre del vendedor';

COMMENT ON COLUMN "Zona-D".zd_vendor.location IS
    'Localización del punto de venta';

COMMENT ON COLUMN "Zona-D".zd_vendor.status IS
    'Active, Inactive';

ALTER TABLE "Zona-D".zd_vendor ADD CONSTRAINT point_sales_pk PRIMARY KEY ( vendor_id );

ALTER TABLE "Zona-D".zd_contacts
    ADD CONSTRAINT zd_contacts_zd_point_sales_fk FOREIGN KEY ( vendor_id )
        REFERENCES "Zona-D".zd_vendor ( vendor_id )
    NOT DEFERRABLE;

ALTER TABLE "Zona-D".zd_fichas
    ADD CONSTRAINT zd_users_point_sales_fk FOREIGN KEY ( vendor_id )
        REFERENCES "Zona-D".zd_vendor ( vendor_id )
    NOT DEFERRABLE;

ALTER TABLE "Zona-D".zd_fichas
    ADD CONSTRAINT zd_users_zd_profiles_fk FOREIGN KEY ( profile )
        REFERENCES "Zona-D".zd_profiles ( code_profile )
    NOT DEFERRABLE;

CREATE OR REPLACE TRIGGER "Zona-D".CONTACT_TGR 
    BEFORE INSERT ON "Zona-D".ZD_CONTACTS 
    FOR EACH ROW 
BEGIN

SELECT CONTACT_SEQ.NEXTVAL
  INTO   :NEW.CONTACT_ID
  FROM   DUAL;
END; 
/

CREATE OR REPLACE NONEDITIONABLE PACKAGE BODY "Zona-D".zona_d_process_pkg AS

    FUNCTION get_ficha_end_date (
        pic_ficha VARCHAR2
    ) RETURN DATE AS
        l_end_date DATE;
    BEGIN
        BEGIN
            SELECT
                sysdate + profiles.profile_time end_date
            INTO l_end_date
            FROM
                zd_fichas   fichas,
                zd_profiles profiles
            WHERE
                    1 = 1
                AND fichas.name = pic_ficha
                AND profiles.code_profile = fichas.profile;

        EXCEPTION
            WHEN no_data_found THEN
                l_end_date := NULL;
                dbms_output.put_line('error');
            WHEN OTHERS THEN
                dbms_output.put_line('error');
        END;

        RETURN l_end_date;
    END;

    PROCEDURE active_new_fichas_ps (
        pic_fichas VARCHAR2
    ) AS

        PRAGMA autonomous_transaction;
        CURSOR cur_fichas_new_cur IS
        SELECT
            ficha,
            status
        FROM
            JSON_TABLE ( pic_fichas, '$.responseFichas[*]'
                COLUMNS (
                    ficha VARCHAR2 ( 100 ) PATH '$.ficha',
                    status VARCHAR2 ( 100 ) PATH '$.status'
                )
            )
        WHERE
            status = 'ACTIVE';

        l_end_date DATE;
    BEGIN
        FOR cur_fichas_new_rec IN cur_fichas_new_cur LOOP
    
        --Obtiene la fecha fin

            l_end_date := get_ficha_end_date(cur_fichas_new_rec.ficha);        
        
        --Actualiza la activación
            UPDATE zd_fichas fichasold
            SET
                fichasold.status = cur_fichas_new_rec.status,
                fichasold.begin_date = sysdate,
                fichasold.end_date = l_end_date
            WHERE
                    1 = 1
                AND fichasold.name = cur_fichas_new_rec.ficha;

        END LOOP;

        COMMIT;
    END active_new_fichas_ps;

END zona_d_process_pkg;
/

BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
            job_name => '"Zona-D"."ZONA-D-ACVIDE-FICHAS"',
            job_type => 'EXECUTABLE',
            job_action => 'C:\\zona-d-schedulers\\zona-d-disable-users.bat',
            number_of_arguments => 0,
            start_date => TO_TIMESTAMP_TZ('2024-11-28 16:50:26.000000000 AMERICA/MEXICO_CITY','YYYY-MM-DD HH24:MI:SS.FF TZR'),
            repeat_interval => 'FREQ=HOURLY;BYDAY=MON,TUE,WED,THU,FRI,SAT,SUN',
            end_date => NULL,
            enabled => FALSE,
            auto_drop => FALSE,
            comments => '');

         
     
 
    DBMS_SCHEDULER.SET_ATTRIBUTE( 
             name => '"Zona-D"."ZONA-D-ACVIDE-FICHAS"', 
             attribute => 'store_output', value => TRUE);
    DBMS_SCHEDULER.SET_ATTRIBUTE( 
             name => '"Zona-D"."ZONA-D-ACVIDE-FICHAS"', 
             attribute => 'logging_level', value => DBMS_SCHEDULER.LOGGING_OFF);
      
   
  
    
    DBMS_SCHEDULER.enable(
             name => '"Zona-D"."ZONA-D-ACVIDE-FICHAS"');
END;
/


-- Informe de Resumen de Oracle SQL Developer Data Modeler: 
-- 
-- CREATE TABLE                             4
-- CREATE INDEX                             1
-- ALTER TABLE                             11
-- CREATE VIEW                              0
-- ALTER VIEW                               0
-- CREATE PACKAGE                           1
-- CREATE PACKAGE BODY                      1
-- CREATE PROCEDURE                         0
-- CREATE FUNCTION                          0
-- CREATE TRIGGER                           1
-- ALTER TRIGGER                            0
-- CREATE COLLECTION TYPE                   0
-- CREATE STRUCTURED TYPE                   0
-- CREATE STRUCTURED TYPE BODY              0
-- CREATE CLUSTER                           0
-- CREATE CONTEXT                           0
-- CREATE DATABASE                          0
-- CREATE DIMENSION                         0
-- CREATE DIRECTORY                         0
-- CREATE DISK GROUP                        0
-- CREATE ROLE                              0
-- CREATE ROLLBACK SEGMENT                  0
-- CREATE SEQUENCE                          1
-- CREATE MATERIALIZED VIEW                 0
-- CREATE MATERIALIZED VIEW LOG             0
-- CREATE SYNONYM                           0
-- CREATE TABLESPACE                        0
-- CREATE USER                              0
-- 
-- DROP TABLESPACE                          0
-- DROP DATABASE                            0
-- 
-- REDACTION POLICY                         0
-- 
-- ORDS DROP SCHEMA                         0
-- ORDS ENABLE SCHEMA                       0
-- ORDS ENABLE OBJECT                       0
-- 
-- ERRORS                                   0
-- WARNINGS                                 0
