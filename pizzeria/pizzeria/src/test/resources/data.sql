CREATE TABLE typesProduits(
    idTypeProduit           INT  AUTO_INCREMENT  NOT NULL  PRIMARY KEY,
    libelleTypeProduit      VARCHAR(150) NOT NULL
)ENGINE=InnoDB, CHARSET = UTF8;

CREATE TABLE produits(
    idProduit               INT  AUTO_INCREMENT  NOT NULL  PRIMARY KEY,
    libelleProduit          VARCHAR(100)  NOT NULL,
    idTypeProduit           INT  NOT NULL,
    prixProduit             FLOAT    ,
    quantite                INT,
    image                   VARCHAR(255)
)ENGINE=InnoDB, CHARSET = UTF8;

CREATE TABLE allergenes(
    idAllergene             INT  AUTO_INCREMENT  NOT NULL  PRIMARY KEY,
    libelleAllergene        VARCHAR(150)  NOT NULL
)ENGINE=InnoDB, CHARSET = UTF8;

CREATE TABLE recettes(
    idRecette               INT  AUTO_INCREMENT  NOT NULL  PRIMARY KEY,
    libelleRecette          VARCHAR(50)  NOT NULL,
    prixRecette             FLOAT  NOT NULL,
    imagePizza              VARCHAR(150),
    dateDebut               DATE,
    dateFin                 DATE
)ENGINE=InnoDB, CHARSET = UTF8;

CREATE TABLE taillesPizzas(
    idTaillePizza               INT  AUTO_INCREMENT  NOT NULL  PRIMARY KEY,
    libelleTaillePizza          INT(2)  NOT NULL,
    tarifSupplement             FLOAT  NOT NULL
)ENGINE=InnoDB, CHARSET = UTF8;

CREATE TABLE roles(
    idRole                  INT  AUTO_INCREMENT  NOT NULL  PRIMARY KEY,
    libelleRole             VARCHAR(50)  NOT NULL
)ENGINE=InnoDB, CHARSET = UTF8;

CREATE TABLE users(
    idUser           INT  AUTO_INCREMENT  NOT NULL PRIMARY KEY,
    nomUser          VARCHAR(100)  NOT NULL,
    prenomUser       VARCHAR(100)  NOT NULL,
    adresse          VARCHAR(255)  NOT NULL,
    codePostal       VARCHAR(5)  NOT NULL,
    ville            VARCHAR(100)  NOT NULL,
    mailUser         VARCHAR(150)  NOT NULL,
    telUser          VARCHAR(10)  NOT NULL,
    motDePasse       VARCHAR(100)  NOT NULL,
    nbPointFidelite  INT(3),
    idRole           INT  NOT NULL
)ENGINE=InnoDB, CHARSET = UTF8;

CREATE TABLE compositions(
    idComposition           INT  AUTO_INCREMENT  NOT NULL  PRIMARY KEY,
    idProduit               INT  NOT NULL,
    idAllergene             INT  NOT NULL
)ENGINE=InnoDB, CHARSET = UTF8;

CREATE TABLE comporteProduitRecette(
    idComporteProduitRecette    INT  AUTO_INCREMENT  NOT NULL  PRIMARY KEY,
    idRecette                   INT  NOT NULL,
    idProduit                   INT  NOT NULL
)ENGINE=InnoDB, CHARSET = UTF8;

CREATE TABLE pizzas(
    idPizza                 INT  AUTO_INCREMENT  NOT NULL  PRIMARY KEY,
    idRecette               INT  NOT NULL,
    idTaillePizza           INT  NOT NULL,
    prix                    FLOAT  NOT NULL
)ENGINE=InnoDB, CHARSET = UTF8;

CREATE TABLE lignesDeCommandes(
    idLigneDeCommande           INT  AUTO_INCREMENT  NOT NULL  PRIMARY KEY,
    idCommande                  INT  NOT NULL,
    quantite                    INT  NOT NULL,
    idProduit                   INT,
    idTaillePizza               INT,
    idPizza                     INT,
    prix                        FLOAT  NOT NULL
)ENGINE=InnoDB, CHARSET = UTF8;

CREATE TABLE commandes(
    idCommande              INT  AUTO_INCREMENT  NOT NULL  PRIMARY KEY,
    numCommande             INT  NOT NULL,
    dateCommande            DATE  NOT NULL,
    nbPointFidelite         INT(3)  NOT NULL,
    horaireLivraison        TIME  NOT NULL,
    idUser                  INT  NOT NULL
)ENGINE=InnoDB, CHARSET = UTF8;


ALTER TABLE users
ADD CONSTRAINT FK_users_roles
FOREIGN KEY (idRole)
REFERENCES roles(idRole);

ALTER TABLE produits
ADD CONSTRAINT FK_produits_typesProduits
FOREIGN KEY (idTypeProduit)
REFERENCES typesProduits(idTypeProduit);

ALTER TABLE pizzas
ADD CONSTRAINT FK_pizzas_recettes
FOREIGN KEY (idRecette)
REFERENCES recettes(idRecette);

ALTER TABLE pizzas
ADD CONSTRAINT FK_pizzas_taillesPizzas
FOREIGN KEY (idTaillePizza)
REFERENCES taillesPizzas(idTaillePizza);

ALTER TABLE commandes
ADD CONSTRAINT FK_commandes_users
FOREIGN KEY (idUser)
REFERENCES users(idUser);

ALTER TABLE comporteProduitRecette
ADD CONSTRAINT FK_comporteProduitRecette_recettes
FOREIGN KEY (idRecette)
REFERENCES recettes(idRecette);

ALTER TABLE comporteProduitRecette
ADD CONSTRAINT FK_comporteProduitRecette_produits
FOREIGN KEY (idProduit)
REFERENCES produits(idProduit);

ALTER TABLE compositions
ADD CONSTRAINT FK_compositions_produits
FOREIGN KEY (idProduit)
REFERENCES produits(idProduit);

ALTER TABLE compositions
ADD CONSTRAINT FK_compositions_allergenes
FOREIGN KEY (idAllergene)
REFERENCES allergenes(idAllergene);

ALTER TABLE lignesDeCommandes
ADD CONSTRAINT FK_lignesDeCommandes_commandes
FOREIGN KEY (idCommande)
REFERENCES commandes(idCommande);

ALTER TABLE lignesDeCommandes
ADD CONSTRAINT FK_lignesDeCommandes_produits
FOREIGN KEY (idProduit)
REFERENCES produits(idProduit);

ALTER TABLE lignesDeCommandes
ADD CONSTRAINT FK_lignesDeCommandes_pizzas
FOREIGN KEY (idPizza)
REFERENCES pizzas(idPizza);

ALTER TABLE lignesDeCommandes
ADD CONSTRAINT FK_lignesDeCommandes_taillesPizzas
FOREIGN KEY (idTaillePizza)
REFERENCES taillesPizzas(idTaillePizza);

CREATE VIEW allergenesproduit AS
SELECT
p.idProduit,
p.libelleProduit,
c.idComposition,
a.idAllergene,
a.libelleAllergene
FROM
produits AS p
INNER JOIN compositions AS c
ON
    p.idProduit = c.idProduit
INNER JOIN allergenes AS a
ON
    c.idAllergene = a.idAllergene;

CREATE VIEW produitsrecettes AS
SELECT
p.idProduit,
p.libelleProduit,
cpr.idComporteProduitRecette,
r.idRecette,
r.libelleRecette,
r.imagePizza
FROM
produits AS p
INNER JOIN comporteProduitRecette AS cpr
ON
    p.idProduit = cpr.idProduit
INNER JOIN recettes AS  r
ON
    cpr.idRecette = r.idRecette;

CREATE VIEW commandePizza AS
SELECT
c.idCommande,
c.numCommande,
c.dateCommande,
c.idUser,
ldc.idLigneDeCommande,
ldc.quantite,
p.idPizza
FROM
commandes AS c
INNER JOIN lignesDeCommandes AS ldc
ON
    c.idCommande = ldc.idCommande
INNER JOIN pizzas AS p
ON
    ldc.idPizza = p.idPizza;


INSERT INTO roles(idRole, libelleRole) VALUES(1, "admin");
INSERT INTO roles(idRole, libelleRole) VALUES(2, "user");