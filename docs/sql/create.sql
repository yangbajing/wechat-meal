CREATE TABLE "t_weixin_account" (
  "id"             VARCHAR            NOT NULL PRIMARY KEY,
  "appId"          VARCHAR            NOT NULL,
  "appSecret"      VARCHAR            NOT NULL,
  "token"          VARCHAR            NOT NULL,
  "encodingAESKey" VARCHAR DEFAULT '' NOT NULL
);
CREATE TABLE "t_user" (
  "id"        BIGSERIAL   NOT NULL PRIMARY KEY,
  "openid"    VARCHAR,
  "email"     VARCHAR     NOT NULL,
  "createdAt" TIMESTAMPTZ NOT NULL
);
CREATE TABLE "t_merchant" (
  "id"        SERIAL      NOT NULL PRIMARY KEY,
  "name"      VARCHAR     NOT NULL,
  "address"   VARCHAR     NOT NULL,
  "remark"    VARCHAR,
  "createdAt" TIMESTAMPTZ NOT NULL
);
CREATE TABLE "t_meal" (
  "id"         BIGSERIAL      NOT NULL PRIMARY KEY,
  "merchantId" INTEGER        NOT NULL,
  "name"       VARCHAR        NOT NULL,
  "price"      DECIMAL(21, 2) NOT NULL,
  "images"     TEXT ARRAY     NOT NULL,
  "remark"     VARCHAR,
  "createdAt"  TIMESTAMPTZ    NOT NULL
);
CREATE TABLE "t_menu" (
  "id"         BIGSERIAL   NOT NULL PRIMARY KEY,
  "merchantId" INTEGER     NOT NULL,
  "type"       VARCHAR     NOT NULL,
  "date"       DATE        NOT NULL,
  "menu"       JSONB       NOT NULL,
  "createdAt"  TIMESTAMPTZ NOT NULL
);
ALTER TABLE "t_meal" ADD CONSTRAINT "t_meal_fk_t_merchant" FOREIGN KEY ("merchantId") REFERENCES "t_merchant" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "t_menu" ADD CONSTRAINT "t_menu_fk_t_merchant" FOREIGN KEY ("merchantId") REFERENCES "t_merchant" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;


INSERT INTO t_merchant ("name", "address", "createdAt") VALUES ('泰芙', '星汇两江', now());
INSERT INTO t_menu ("merchantId", "type", "date", "menu", "createdAt") VALUES (1, 'L', now(), '[{"name":"冬阴功米线","price":25},{"name":"咖喱鸡套饭","price":25},{"name":"印尼炒饭套","price":23},{"name":"泰式猪扒饭","price":22},{"name":"泰式炒鲜鱿饭","price":24},{"name":"红咖喱牛腩","price":28},{"name":"泰式鱼扒饭","price":22},{"name":"泰式烧茄子","price":18},{"name":"海鲜烧豆腐","price":20}]', now());