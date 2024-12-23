--Implementación de promociones

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


ALTER TABLE "Zona-D".zd_fichas ADD (
    promotion_code VARCHAR2(50 CHAR)
);

COMMENT ON COLUMN "Zona-D".zd_fichas.promotion_code IS
    'Código de la promoción';
	
ALTER TABLE "Zona-D".zd_fichas
    ADD CONSTRAINT zd_fichas_zd_promotions_fk FOREIGN KEY ( promotion_code )
        REFERENCES "Zona-D".zd_promotions ( promotion_code )
    NOT DEFERRABLE;
ALTER TABLE "Zona-D".zd_fichas RENAME CONSTRAINT zd_users_point_sales_fk TO zd_fichas_zd_vendor_fk;



--Paquete
create or replace NONEDITIONABLE PACKAGE BODY          "Zona-D".ZONA_D_PROCESS_PKG AS

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

    PROCEDURE disabled_fichas_ps (
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
            status = 'FINISHED';

    BEGIN
        FOR cur_fichas_new_rec IN cur_fichas_new_cur LOOP

            --Actualiza la activación
            UPDATE zd_fichas fichasold
            SET
                fichasold.status = cur_fichas_new_rec.status
            WHERE
                    1 = 1
                AND fichasold.name = cur_fichas_new_rec.ficha;

        END LOOP;

        COMMIT;
    END;

    PROCEDURE register_fichas_ps (
        pic_fichas VARCHAR2
    ) AS

        PRAGMA autonomous_transaction;
        CURSOR new_fichas_cur IS
        SELECT
            title,
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
            username12,
            promotion
        FROM
            JSON_TABLE ( pic_fichas, '$[*]'
                COLUMNS (
                    title VARCHAR2 ( 100 ) PATH '$.title',
                    profile VARCHAR2 ( 100 ) PATH '$.profileCode',
                    vendor VARCHAR2 ( 100 ) PATH '$.vendor',
                    username1 VARCHAR2 ( 100 ) PATH '$.username1',
                    username2 VARCHAR2 ( 100 ) PATH '$.username2',
                    username3 VARCHAR2 ( 100 ) PATH '$.username3',
                    username4 VARCHAR2 ( 100 ) PATH '$.username4',
                    username5 VARCHAR2 ( 100 ) PATH '$.username5',
                    username6 VARCHAR2 ( 100 ) PATH '$.username6',
                    username7 VARCHAR2 ( 100 ) PATH '$.username7',
                    username8 VARCHAR2 ( 100 ) PATH '$.username8',
                    username9 VARCHAR2 ( 100 ) PATH '$.username9',
                    username10 VARCHAR2 ( 100 ) PATH '$.username10',
                    username11 VARCHAR2 ( 100 ) PATH '$.username11',
                    username12 VARCHAR2 ( 100 ) PATH '$.username12',
                    promotion  VARCHAR2 ( 100 ) PATH '$.promotion'
                )
            );

    BEGIN
        BEGIN
            --Itera sobre la plantilla de usuarios
            FOR new_fichas_rec IN new_fichas_cur LOOP
                INSERT INTO zd_fichas (
                    name,
                    profile,
                    vendor_id,
                    promotion_code
                ) VALUES (
                    new_fichas_rec.username1,
                    new_fichas_rec.profile,
                    new_fichas_rec.vendor,
                    new_fichas_rec.promotion
                );

                INSERT INTO zd_fichas (
                    name,
                    profile,
                    vendor_id,
                    promotion_code
                ) VALUES (
                    new_fichas_rec.username2,
                    new_fichas_rec.profile,
                    new_fichas_rec.vendor,
                    new_fichas_rec.promotion
                );

                INSERT INTO zd_fichas (
                    name,
                    profile,
                    vendor_id,
                    promotion_code
                ) VALUES (
                    new_fichas_rec.username3,
                    new_fichas_rec.profile,
                    new_fichas_rec.vendor,
                    new_fichas_rec.promotion
                );

                INSERT INTO zd_fichas (
                    name,
                    profile,
                    vendor_id,
                    promotion_code
                ) VALUES (
                    new_fichas_rec.username4,
                    new_fichas_rec.profile,
                    new_fichas_rec.vendor,
                    new_fichas_rec.promotion
                );

                INSERT INTO zd_fichas (
                    name,
                    profile,
                    vendor_id,
                    promotion_code
                ) VALUES (
                    new_fichas_rec.username5,
                    new_fichas_rec.profile,
                    new_fichas_rec.vendor,
                    new_fichas_rec.promotion
                );

                INSERT INTO zd_fichas (
                    name,
                    profile,
                    vendor_id,
                    promotion_code
                ) VALUES (
                    new_fichas_rec.username6,
                    new_fichas_rec.profile,
                    new_fichas_rec.vendor,
                    new_fichas_rec.promotion
                );

                INSERT INTO zd_fichas (
                    name,
                    profile,
                    vendor_id,
                    promotion_code
                ) VALUES (
                    new_fichas_rec.username7,
                    new_fichas_rec.profile,
                    new_fichas_rec.vendor,
                    new_fichas_rec.promotion
                );

                INSERT INTO zd_fichas (
                    name,
                    profile,
                    vendor_id,
                    promotion_code
                ) VALUES (
                    new_fichas_rec.username8,
                    new_fichas_rec.profile,
                    new_fichas_rec.vendor,
                    new_fichas_rec.promotion
                );

                INSERT INTO zd_fichas (
                    name,
                    profile,
                    vendor_id,
                    promotion_code
                ) VALUES (
                    new_fichas_rec.username9,
                    new_fichas_rec.profile,
                    new_fichas_rec.vendor,
                    new_fichas_rec.promotion
                );

                INSERT INTO zd_fichas (
                    name,
                    profile,
                    vendor_id,
                    promotion_code
                ) VALUES (
                    new_fichas_rec.username10,
                    new_fichas_rec.profile,
                    new_fichas_rec.vendor,
                    new_fichas_rec.promotion
                );

                INSERT INTO zd_fichas (
                    name,
                    profile,
                    vendor_id,
                    promotion_code
                ) VALUES (
                    new_fichas_rec.username11,
                    new_fichas_rec.profile,
                    new_fichas_rec.vendor,
                    new_fichas_rec.promotion
                );

                INSERT INTO zd_fichas (
                    name,
                    profile,
                    vendor_id,
                    promotion_code
                ) VALUES (
                    new_fichas_rec.username12,
                    new_fichas_rec.profile,
                    new_fichas_rec.vendor,
                    new_fichas_rec.promotion
                );

            END LOOP;

            COMMIT;
        EXCEPTION
            WHEN OTHERS THEN
                dbms_output.put_line('Error'
                                     || sqlcode
                                     || ' -ERROR- '
                                     || sqlerrm);
        END;
    END;

END zona_d_process_pkg;

/
show errors;