CREATE TABLE KOM_ORGANIZATION_CONTEXTS_T (
		ID NUMBER(8) NOT NULL,
		NAME VARCHAR(255) NOT NULL,
		DESCRIPTION VARCHAR(255) NOT NULL,
		OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
        VER_NBR NUMBER(8) DEFAULT 1 NOT NULL,
		CONSTRAINT KOM_ORGANIZATION_CONTEXTS_PK PRIMARY KEY (ID)
)
/