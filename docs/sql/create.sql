CREATE TABLE "t_weixin_account" (
  "id"             VARCHAR            NOT NULL PRIMARY KEY,
  "appId"          VARCHAR            NOT NULL,
  "appSecret"      VARCHAR            NOT NULL,
  "token"          VARCHAR            NOT NULL,
  "encodingAESKey" VARCHAR DEFAULT '' NOT NULL
);
CREATE TABLE "t_merchant" (
  "id"        SERIAL    NOT NULL PRIMARY KEY,
  "name"      VARCHAR   NOT NULL,
  "remark"    VARCHAR,
  "createdAt" TIMESTAMP NOT NULL
);
CREATE TABLE "t_meal" (
  "id"         BIGSERIAL      NOT NULL PRIMARY KEY,
  "merchantId" INTEGER        NOT NULL,
  "name"       VARCHAR        NOT NULL,
  "price"      DECIMAL(21, 2) NOT NULL,
  "images"     TEXT ARRAY     NOT NULL,
  "remark"     VARCHAR,
  "createdAt"  TIMESTAMP      NOT NULL
);
CREATE TABLE "t_menu" (
  "id"         BIGSERIAL NOT NULL PRIMARY KEY,
  "merchantId" INTEGER   NOT NULL,
  "type"       VARCHAR   NOT NULL,
  "date"       DATE      NOT NULL,
  "menu"       JSONB     NOT NULL,
  "createdAt"  TIMESTAMP NOT NULL
);
ALTER TABLE "t_meal" ADD CONSTRAINT "t_meal_fk_t_merchant" FOREIGN KEY ("merchantId") REFERENCES "t_merchant" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "t_menu" ADD CONSTRAINT "t_menu_fk_t_merchant" FOREIGN KEY ("merchantId") REFERENCES "t_merchant" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;