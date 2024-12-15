


-- Generado por Oracle SQL Developer Data Modeler 23.1.0.087.0806
--   en:        2024-11-24 23:25:26 CST
--   sitio:      Oracle Database 12cR2
--   tipo:      Oracle Database 12cR2

alter session set "_ORACLE_SCRIPT"=true; 
CREATE USER "Zona-D" IDENTIFIED BY "1234";


ALTER USER "Zona-D"
DEFAULT TABLESPACE "USERS"
TEMPORARY TABLESPACE "TEMP"
ACCOUNT UNLOCK ;


-- Generado por Oracle SQL Developer Data Modeler 23.1.0.087.0806
--   en:        2024-11-28 20:01:47 CST
--   sitio:      Oracle Database 12cR2
--   tipo:      Oracle Database 12cR2



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

create or replace NONEDITIONABLE PACKAGE ZONA_D_PROCESS_PKG AS 
    
    /**
     * Procesa las fichas nuevas 
    **/
    PROCEDURE ACTIVE_NEW_FICHAS_PS(PIC_FICHAS VARCHAR2);
    
    PROCEDURE DISABLED_FICHAS_PS(PIC_FICHAS VARCHAR2);
    
    PROCEDURE REGISTER_FICHAS_PS(PIC_FICHAS VARCHAR2);

END ZONA_D_PROCESS_PKG;
/
create or replace NONEDITIONABLE PACKAGE BODY ZONA_D_PROCESS_PKG AS
    
    
    
    FUNCTION GET_FICHA_END_DATE(PIC_FICHA VARCHAR2)RETURN DATE 
    AS
        L_END_DATE DATE;
    BEGIN
        
        BEGIN
            SELECT SYSDATE + Profiles.PROFILE_TIME END_DATE
            INTO L_END_DATE
            FROM ZD_FICHAS Fichas,
                 ZD_PROFILES Profiles
            WHERE 1 = 1
              AND Fichas.NAME  = PIC_FICHA
              AND Profiles.CODE_PROFILE = Fichas.PROFILE;
        EXCEPTION 
            WHEN NO_DATA_FOUND THEN
                L_END_DATE := NULL;
                dbms_output.put_line('error');
            WHEN OTHERS THEN 
                dbms_output.put_line('error');
        END;
        
    
    RETURN L_END_DATE;
    
    END;
    
    PROCEDURE ACTIVE_NEW_FICHAS_PS(PIC_FICHAS VARCHAR2) AS
        PRAGMA AUTONOMOUS_TRANSACTION;
    
        cursor cur_fichas_new_cur is
            select Ficha,
                      Status
                from json_table(PIC_FICHAS,'$.responseFichas[*]'
                                columns (ficha varchar2(100) path '$.ficha' ,
                                         status varchar2(100) path '$.status' ))
            WHERE Status ='ACTIVE';
                                         
            L_END_DATE DATE;
    BEGIN
     
        FOR cur_fichas_new_rec IN cur_fichas_new_cur
        LOOP
        
            --Obtiene la fecha fin
            
            L_END_DATE := GET_FICHA_END_DATE(cur_fichas_new_rec.Ficha);        
            
            --Actualiza la activación
            UPDATE ZD_FICHAS FichasOld
                SET FichasOld.STATUS = cur_fichas_new_rec.status,
                    FichasOld.begin_date = sysdate,
                    FichasOld.end_date = L_END_DATE
            WHERE 1 = 1
              AND FichasOld.NAME = cur_fichas_new_rec.FICHA;
        
        END LOOP;
        
        COMMIT;
    
    END ACTIVE_NEW_FICHAS_PS;
    
    
    PROCEDURE DISABLED_FICHAS_PS(PIC_FICHAS VARCHAR2) AS
        PRAGMA AUTONOMOUS_TRANSACTION;
    
    cursor cur_fichas_new_cur is
        select Ficha,
                  Status
            from json_table(PIC_FICHAS,'$.responseFichas[*]'
                            columns (ficha varchar2(100) path '$.ficha' ,
                                     status varchar2(100) path '$.status' ))
        WHERE Status ='FINISHED';
                                     
    BEGIN
     
        FOR cur_fichas_new_rec IN cur_fichas_new_cur
        LOOP
            
            --Actualiza la activación
            UPDATE ZD_FICHAS FichasOld
                SET FichasOld.STATUS = cur_fichas_new_rec.status                
            WHERE 1 = 1
              AND FichasOld.NAME = cur_fichas_new_rec.FICHA;
        
        END LOOP;
        
        COMMIT;
    
    END;
    
    PROCEDURE REGISTER_FICHAS_PS(PIC_FICHAS VARCHAR2)AS
    
        PRAGMA AUTONOMOUS_TRANSACTION;
    
        CURSOR NEW_FICHAS_CUR IS
            select title,
                   profile,
                   vendor,
                   username1,
                   username2,
                   username3,
                   username4,
                   username5,
                   username6,
                   username7,
                   username8,
                   username9,
                   username10,
                   username11,
                   username12
                   
                from json_table(PIC_FICHAS,'$[*]'
                columns (title varchar2(100) path '$.title' ,
                         profile varchar2(100) path '$.profileCode',
                         
                         vendor varchar2(100) path '$.vendor',
                         username1 varchar2(100) path '$.username1',
                         username2 varchar2(100) path '$.username2',
                         username3 varchar2(100) path '$.username3',
                         username4 varchar2(100) path '$.username4',
                         username5 varchar2(100) path '$.username5',
                         username6 varchar2(100) path '$.username6',
                         username7 varchar2(100) path '$.username7',
                         username8 varchar2(100) path '$.username8',
                         username9 varchar2(100) path '$.username9',
                         username10 varchar2(100) path '$.username10',
                         username11 varchar2(100) path '$.username11',
                         username12 varchar2(100) path '$.username12'));


    
    BEGIN
    
        BEGIN
            --Itera sobre la plantilla de usuarios
            FOR NEW_FICHAS_REC IN NEW_FICHAS_CUR LOOP
                
                INSERT INTO ZD_FICHAS (NAME,PROFILE,VENDOR_ID) VALUES(NEW_FICHAS_REC.USERNAME1,
                                                                      NEW_FICHAS_REC.PROFILE,
                                                                      NEW_FICHAS_REC.VENDOR);
                
                INSERT INTO ZD_FICHAS (NAME,PROFILE,VENDOR_ID) VALUES(NEW_FICHAS_REC.USERNAME2,
                                                                      NEW_FICHAS_REC.PROFILE,
                                                                      NEW_FICHAS_REC.VENDOR);
                                                                      
                INSERT INTO ZD_FICHAS (NAME,PROFILE,VENDOR_ID) VALUES(NEW_FICHAS_REC.USERNAME3,
                                                                      NEW_FICHAS_REC.PROFILE,
                                                                      NEW_FICHAS_REC.VENDOR);
                                                                      
                INSERT INTO ZD_FICHAS (NAME,PROFILE,VENDOR_ID) VALUES(NEW_FICHAS_REC.USERNAME4,
                                                                      NEW_FICHAS_REC.PROFILE,
                                                                      NEW_FICHAS_REC.VENDOR);
                
                INSERT INTO ZD_FICHAS (NAME,PROFILE,VENDOR_ID) VALUES(NEW_FICHAS_REC.USERNAME5,
                                                                      NEW_FICHAS_REC.PROFILE,
                                                                      NEW_FICHAS_REC.VENDOR);
                                                                      
                INSERT INTO ZD_FICHAS (NAME,PROFILE,VENDOR_ID) VALUES(NEW_FICHAS_REC.USERNAME6,
                                                                      NEW_FICHAS_REC.PROFILE,
                                                                      NEW_FICHAS_REC.VENDOR);
                
                INSERT INTO ZD_FICHAS (NAME,PROFILE,VENDOR_ID) VALUES(NEW_FICHAS_REC.USERNAME7,
                                                                      NEW_FICHAS_REC.PROFILE,
                                                                      NEW_FICHAS_REC.VENDOR);
                
                INSERT INTO ZD_FICHAS (NAME,PROFILE,VENDOR_ID) VALUES(NEW_FICHAS_REC.USERNAME8,
                                                                      NEW_FICHAS_REC.PROFILE,
                                                                      NEW_FICHAS_REC.VENDOR);
                                                                      
                INSERT INTO ZD_FICHAS (NAME,PROFILE,VENDOR_ID) VALUES(NEW_FICHAS_REC.USERNAME9,
                                                                      NEW_FICHAS_REC.PROFILE,
                                                                      NEW_FICHAS_REC.VENDOR);                                                      
                 
                INSERT INTO ZD_FICHAS (NAME,PROFILE,VENDOR_ID) VALUES(NEW_FICHAS_REC.USERNAME10,
                                                                      NEW_FICHAS_REC.PROFILE,
                                                                      NEW_FICHAS_REC.VENDOR);    
                                                                      
                INSERT INTO ZD_FICHAS (NAME,PROFILE,VENDOR_ID) VALUES(NEW_FICHAS_REC.USERNAME11,
                                                                      NEW_FICHAS_REC.PROFILE,
                                                                      NEW_FICHAS_REC.VENDOR);                                                      
                 
                INSERT INTO ZD_FICHAS (NAME,PROFILE,VENDOR_ID) VALUES(NEW_FICHAS_REC.USERNAME12,
                                                                      NEW_FICHAS_REC.PROFILE,
                                                                      NEW_FICHAS_REC.VENDOR);                                                                       
            END LOOP;
            
            COMMIT;
        EXCEPTION WHEN OTHERS THEN
            dbms_output.put_line('Error'||SQLCODE||' -ERROR- '||SQLERRM);
        END;
    END;

END ZONA_D_PROCESS_PKG;
/

BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
            job_name => '"Zona-D"."ZONA_D_ACTIVE_USERS"',
            job_type => 'EXECUTABLE',
            job_action => 'C:\\zona-d-schedulers\\zona-d-active-users.bat',
            number_of_arguments => 0,
            start_date => TO_TIMESTAMP_TZ('2024-11-28 22:33:23.977000000 AMERICA/MEXICO_CITY','YYYY-MM-DD HH24:MI:SS.FF TZR'),
            repeat_interval => 'FREQ=HOURLY;BYTIME=2250;BYDAY=MON,TUE,WED,THU,FRI,SAT,SUN',
            end_date => NULL,
            enabled => FALSE,
            auto_drop => FALSE,
            comments => '');

         
     
 
    DBMS_SCHEDULER.SET_ATTRIBUTE( 
             name => '"Zona-D".""', 
             attribute => 'store_output', value => TRUE);
    DBMS_SCHEDULER.SET_ATTRIBUTE( 
             name => '"Zona-D"."ZONA_D_ACTIVE_USERS"', 
             attribute => 'logging_level', value => DBMS_SCHEDULER.LOGGING_OFF);
      
   
  
    
    DBMS_SCHEDULER.enable(
             name => '"Zona-D"."ZONA_D_ACTIVE_USERS"');
END;

/


Insert into "Zona-D".ZD_PROFILES (CODE_PROFILE,PROFILE_NAME,PROFILE_TIME,STATUS) values ('SEM','Zona-D-Semanal-Corrido',7,'Active');
Insert into "Zona-D".ZD_PROFILES (CODE_PROFILE,PROFILE_NAME,PROFILE_TIME,STATUS) values ('QUI','Zona-D-Quincenal-Corrido',15,'Active');
Insert into "Zona-D".ZD_PROFILES (CODE_PROFILE,PROFILE_NAME,PROFILE_TIME,STATUS) values ('MEN','Zona-D-Mensual-Corrido',30,'Active');
Insert into "Zona-D".ZD_PROFILES (CODE_PROFILE,PROFILE_NAME,PROFILE_TIME,STATUS) values ('H24','Zona-D-24Hr-Corrido',1,'Active');

commit;

Insert into "Zona-D".ZD_VENDOR (VENDOR_ID,VENDOR_NAME,LOCATION,STATUS) values ('IRIS','Iris','Taquería Palillo','Active');
Insert into "Zona-D".ZD_VENDOR (VENDOR_ID,VENDOR_NAME,LOCATION,STATUS) values ('MIGUEL','Iris','Panadería frente a la granda','Active');
commit;



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


--CAMBIOS 14/12/2024
-- Generado por Oracle SQL Developer Data Modeler 23.1.0.087.0806
--   en:        2024-12-14 22:14:21 CST
--   sitio:      Oracle Database 21c
--   tipo:      Oracle Database 21c




CREATE TABLE "Zona-D".zd_promotions (
    promotion_code VARCHAR2(50 CHAR) NOT NULL,
    name           VARCHAR2(50 CHAR),
    description    VARCHAR2(500 CHAR),
    discount       NUMBER(2),
    status         VARCHAR2(10 CHAR) DEFAULT 'Active'
)
LOGGING;

ALTER TABLE "Zona-D".zd_promotions
    ADD CONSTRAINT status_vendor_2 CHECK ( status IN ( 'Active', 'Inactive' ) );

COMMENT ON COLUMN "Zona-D".zd_promotions.promotion_code IS
    'Código de la promoción';

COMMENT ON COLUMN "Zona-D".zd_promotions.name IS
    'Nombre de la promoción';

COMMENT ON COLUMN "Zona-D".zd_promotions.description IS
    'Descripción de la promoción';

COMMENT ON COLUMN "Zona-D".zd_promotions.discount IS
    'Porcentaje de descuento';

COMMENT ON COLUMN "Zona-D".zd_promotions.status IS
    'Active, Inactive';

ALTER TABLE "Zona-D".zd_promotions ADD CONSTRAINT zd_promotions_pk PRIMARY KEY ( promotion_code );

--2



ALTER TABLE "Zona-D".zd_fichas MOVE
    STORAGE ( INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 );
ALTER TABLE "Zona-D".zd_fichas MODIFY (
    profile null
);
ALTER TABLE "Zona-D".zd_fichas MODIFY (
    vendor_id null
);
ALTER TABLE "Zona-D".zd_fichas MODIFY (
    creation_date DEFAULT sysdate
);
ALTER TABLE "Zona-D".zd_fichas ADD (
    comments VARCHAR2(2000 BYTE)
);
ALTER TABLE "Zona-D".zd_fichas ADD (
    is_payment VARCHAR2(5 BYTE) DEFAULT 'N'
);
ALTER TABLE "Zona-D".zd_fichas ADD (
    promotion_code VARCHAR2(50 CHAR)
);

COMMENT ON COLUMN "Zona-D".zd_fichas.promotion_code IS
    'Código de la promoción';
ALTER INDEX "Zona-D".users_pk REBUILD
    STORAGE ( INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 );
ALTER INDEX "Zona-D".users_pk REBUILD
    STORAGE ( INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 );
ALTER TABLE "Zona-D".zd_fichas ADD CHECK ( "NAME" IS NOT NULL );
ALTER TABLE "Zona-D".zd_fichas ADD CHECK ( "PROFILE" IS NOT NULL );
ALTER TABLE "Zona-D".zd_fichas ADD CHECK ( "VENDOR_ID" IS NOT NULL );
ALTER TABLE "Zona-D".zd_fichas ADD CHECK ( "CREATION_DATE" IS NOT NULL );
ALTER TABLE "Zona-D".zd_fichas
    ADD CONSTRAINT zd_fichas_zd_promotions_fk FOREIGN KEY ( promotion_code )
        REFERENCES "Zona-D".zd_promotions ( promotion_code )
    NOT DEFERRABLE;
ALTER TABLE "Zona-D".zd_fichas RENAME CONSTRAINT zd_users_point_sales_fk TO zd_fichas_zd_vendor_fk;

