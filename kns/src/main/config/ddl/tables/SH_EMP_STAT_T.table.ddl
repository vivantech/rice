CREATE TABLE SH_EMP_STAT_T (
        EMP_STAT_CD                    VARCHAR2(1) CONSTRAINT SH_EMP_STAT_TN1 NOT NULL,
        EMP_STAT_DESC                  VARCHAR2(250),
        ROW_ACTV_IND                   VARCHAR2(1),
        OBJ_ID                         VARCHAR2(36) DEFAULT SYS_GUID() CONSTRAINT SH_EMP_STAT_TN2 NOT NULL,
        VER_NBR                        NUMBER(8) DEFAULT 1 CONSTRAINT SH_EMP_STAT_TN3 NOT NULL,
     CONSTRAINT SH_EMP_STAT_TP1 PRIMARY KEY (EMP_STAT_CD),
     CONSTRAINT SH_EMP_STAT_TC0 UNIQUE (OBJ_ID)
)
/