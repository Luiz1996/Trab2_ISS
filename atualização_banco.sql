DROP SCHEMA IF EXISTS `bibliotec`;
COMMIT;

CREATE DATABASE  IF NOT EXISTS `bibliotec`;
COMMIT;

USE `bibliotec`;
COMMIT;

CREATE TABLE `usuarios` (
    `codusuario` INT(11) NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(100) NOT NULL,
    `usuario` VARCHAR(30) NOT NULL,
    `senha` VARCHAR(32) NOT NULL,
    `nome` VARCHAR(60) NOT NULL,
    `rg` VARCHAR(10) NOT NULL,
    `cpf` VARCHAR(15) NOT NULL,
    `endereco` VARCHAR(100) NOT NULL,
    `cep` VARCHAR(9) NOT NULL,
    `cidade` VARCHAR(60) NOT NULL,
    `estado` VARCHAR(20) NOT NULL,
    `permissao` INT(11) NOT NULL,
    `ativo` INT(11) NOT NULL,
    `datacad` DATE NOT NULL,
    `dataalt` DATE DEFAULT NULL,
    `datanasc` DATE NOT NULL,
    `jaativado` INT(11) NOT NULL,
    PRIMARY KEY (`codusuario` , `email` , `usuario`),
    UNIQUE KEY `codusuario_UNIQUE` (`codusuario`),
    UNIQUE KEY `email_UNIQUE` (`email`),
    UNIQUE KEY `usuario_UNIQUE` (`usuario`),
    UNIQUE KEY `rg_UNIQUE` (`rg`),
    UNIQUE KEY `cpf_UNIQUE` (`cpf`)
);

LOCK TABLES `usuarios` WRITE;
INSERT INTO `usuarios` VALUES (1,'BIBLIOTECARIO_UEM_TESTE@HOTMAIL.COM','1','c4ca4238a0b923820dcc509a6f75849b','Bibliotecário','111111111','11111111111','Rua A','87000000','Maringá','PR',1,1,'2019-05-01',NULL,'2019-01-01',1),(2,'BALCONISTA_UEM_TESTE@HOTMAIL.COM','2','c81e728d9d4c2f636f067f89cc14862c','Balconista','222222222','22222222222','Rua B','87000000','Maringá','PR',2,1,'2019-05-01',NULL,'2019-01-01',1),(3,'ALUNO_UEM_TESTE@HOTMAIL.COM','3','eccbc87e4b5ce2fe28308fd9f2a7baf3','Aluno','333333333','33333333333','Rua C','87000000','Maringá','PR',3,1,'2019-05-01',NULL,'2019-01-01',1);
UNLOCK TABLES;
COMMIT;

DROP TABLE IF EXISTS `livro`;
COMMIT;

CREATE TABLE `livro` (
    `codlivro` INT(11) NOT NULL AUTO_INCREMENT,
    `codcatalogacao` VARCHAR(15) NOT NULL,
    `numchamada` VARCHAR(15) NOT NULL,
    `titulo` VARCHAR(60) NOT NULL,
    `autor` VARCHAR(60) NOT NULL,
    `editora` VARCHAR(60) NOT NULL,
    `anolancamento` VARCHAR(4) NOT NULL,
    `cidade` VARCHAR(60) NOT NULL,
    `volume` INT(2) NOT NULL,
    `ativo` INT(1) NOT NULL,
    `datacad` DATE NOT NULL,
    `dataalt` DATE DEFAULT NULL,
    `disponibilidade` INT(11) NOT NULL,
    PRIMARY KEY (`codlivro` , `codcatalogacao` , `numchamada`),
    UNIQUE KEY `codlivro_UNIQUE` (`codlivro`),
    UNIQUE KEY `codcatalogacao_UNIQUE` (`codcatalogacao`),
    UNIQUE KEY `numchamada_UNIQUE` (`numchamada`)
);
COMMIT;

LOCK TABLES `livro` WRITE;
INSERT INTO `livro` VALUES (1,'452K','978853800365-8','Dom Casmurro','Machado de Assis','Giranda Cultural','2008','São Paulo',1,1,'2019-05-01',NULL,1),(2,'A568m','857175057-2','Macunaíma','Mário de Andrade','Venha Ler','2001','Belo Horizonte',1,1,'2019-05-01',NULL,1),(3,'A353i','859861010-0','Iracema','José de Alencar','Editora Avenida','2005','Jaraguá do Sul',1,1,'2019-05-01',NULL,1),(4,'Y71c','978854310468-3','A Cabana','William P. Young','Sextante','2016','Rio de Janeiro',1,1,'2019-05-01',NULL,1),(5,'L732t','978852541278-2','Triste Fim de Policarpo Quaresma','Lima Barreto','L&PM','2009','Porto Alegre',1,1,'2019-05-01',NULL,1),(6,'M121m','859855906-7','A Moreninha','Joaquim Manoel de Macedo','Clássicos da Literatura','1998','Maringá',1,1,'2019-05-01',NULL,1),(7,'L7531','853250813-8','Laços de Família','Clarice Lispector','Rocco','1998','Rio de Janeiro',1,1,'2019-05-01',NULL,1),(8,'NC','857054033-7','Algoritmos e Estruturas de Dados','Niklaus Wirth','PHB','1989','Rio de Janeiro',1,1,'2019-05-01',NULL,1),(9,'R143m','850100916-4','Memórias do Cárcere','Graciliano Ramos','Record','2005','São Paulo',1,1,'2019-05-01',NULL,1),(10,'M453l','85497813-5','Lucíola','José de Alencar ','Ática','1987','São Paulo',1,1,'2019-05-05',NULL,1);
UNLOCK TABLES;
COMMIT;

DROP TABLE IF EXISTS `emprestimo`;
COMMIT;

CREATE TABLE `emprestimo` (
    `codemprestimo` INT(11) NOT NULL AUTO_INCREMENT,
    `codusuario` INT(11) NOT NULL,
    `codlivro` INT(11) NOT NULL,
    `dataemp` DATE NOT NULL,
    `datadev` DATE NOT NULL,
    `dataalt` DATE DEFAULT NULL,
    `ativo` INT(11) DEFAULT NULL,
    PRIMARY KEY (`codemprestimo`),
    UNIQUE KEY `codemprestimo_UNIQUE` (`codemprestimo`),
    KEY `emp-usuario_idx` (`codusuario`),
    KEY `emp-livro_idx` (`codlivro`),
    CONSTRAINT `emp-livro` FOREIGN KEY (`codlivro`)
        REFERENCES `livro` (`codlivro`),
    CONSTRAINT `emp-usuario` FOREIGN KEY (`codusuario`)
        REFERENCES `usuarios` (`codusuario`)
);
COMMIT;

LOCK TABLES `emprestimo` WRITE;
UNLOCK TABLES;
COMMIT;

ALTER TABLE `bibliotec`.`emprestimo` AUTO_INCREMENT = 1;
ALTER TABLE `bibliotec`.`usuarios` AUTO_INCREMENT = 4;
ALTER TABLE `bibliotec`.`livro` AUTO_INCREMENT = 11;
COMMIT;

DROP EVENT IF EXISTS `RESETA_RESERVAS`;
CREATE EVENT RESETA_RESERVAS
ON SCHEDULE EVERY 15 MINUTE
STARTS CURRENT_TIMESTAMP
ON COMPLETION PRESERVE
DO
	UPDATE `bibliotec`.`reserva` SET DATAALT = CURRENT_DATE(), ATIVO = '0' WHERE DATARES < CURRENT_DATE();
COMMIT;    

DROP EVENT IF EXISTS `RESETA_BLOQUEADOS`;
CREATE EVENT RESETA_BLOQUEADOS
ON SCHEDULE EVERY 1 MINUTE
STARTS CURRENT_TIMESTAMP
ON COMPLETION PRESERVE
DO
	UPDATE `bibliotec`.`usuarios` SET `bloq` = NULL, `datahorabloq` = NULL WHERE TIMESTAMPDIFF (MINUTE, `datahorabloq`, current_timestamp()) > '5'; 
COMMIT;    

SET GLOBAL EVENT_SCHEDULER = 1;
COMMIT;

SET GLOBAL event_scheduler = ON;
COMMIT;

CREATE TABLE `bibliotec`.`reserva` (
    `codreserva` INT NOT NULL,
    `codlivro` INT NOT NULL,
    `codusuario` INT NOT NULL,
    `datacad` DATE NOT NULL,
    `ativo` INT NOT NULL,
    PRIMARY KEY (`codreserva`),
    INDEX `res-livro_idx` (`codlivro` ASC),
    INDEX `res-usuario_idx` (`codusuario` ASC),
    CONSTRAINT `res-livro` FOREIGN KEY (`codlivro`)
        REFERENCES `bibliotec`.`livro` (`codlivro`)
        ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `res-usuario` FOREIGN KEY (`codusuario`)
        REFERENCES `bibliotec`.`usuarios` (`codusuario`)
        ON DELETE NO ACTION ON UPDATE NO ACTION
);
COMMIT;

ALTER TABLE `bibliotec`.`reserva` 
CHANGE COLUMN `codreserva` `codreserva` INT(11) NOT NULL AUTO_INCREMENT ;
COMMIT;

CREATE TABLE `bibliotec`.`multa` (
    `codmulta` INT NOT NULL AUTO_INCREMENT,
    `codlivro` INT NOT NULL,
    `codusuario` INT NOT NULL,
    `datacad` DATE NOT NULL,
    `dataalt` DATE NOT NULL,
    `valor` DOUBLE NOT NULL,
    `ativo` INT NOT NULL,
    PRIMARY KEY (`codmulta`),
    INDEX `mul-livro_idx` (`codlivro` ASC),
    INDEX `mul-usuario_idx` (`codusuario` ASC),
    CONSTRAINT `mul-livro` FOREIGN KEY (`codlivro`)
        REFERENCES `bibliotec`.`livro` (`codlivro`)
        ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `mul-usuario` FOREIGN KEY (`codusuario`)
        REFERENCES `bibliotec`.`usuarios` (`codusuario`)
        ON DELETE NO ACTION ON UPDATE NO ACTION
);
COMMIT;

ALTER TABLE `bibliotec`.`reserva` 
ADD COLUMN `dataalt` DATE NULL AFTER `datacad`;
COMMIT;

ALTER TABLE `bibliotec`.`reserva` 
CHANGE COLUMN `dataalt` `dataalt` DATE NOT NULL ;
COMMIT;  
 
CREATE DATABASE  IF NOT EXISTS `bibliotec`;
USE `bibliotec`; 
COMMIT;

ALTER TABLE `bibliotec`.`reserva` 
CHANGE COLUMN `dataalt` `dataalt` DATE NULL ;
COMMIT;

ALTER TABLE `bibliotec`.`multa` 
CHANGE COLUMN `dataalt` `dataalt` DATE NULL ;
COMMIT;

ALTER TABLE `bibliotec`.`reserva` 
ADD COLUMN `datares` DATE NULL AFTER `dataalt`;
COMMIT;

CREATE TABLE `bibliotec`.`cotacao` (
    `codcotacao` INT NOT NULL AUTO_INCREMENT,
    `valor` DOUBLE NULL,
    `datacad` DATE NULL,
    `dataalt` DATE NULL,
    `ativo` INT NULL,
    PRIMARY KEY (`codcotacao`)
);
COMMIT;

ALTER TABLE `bibliotec`.`cotacao` 
CHANGE COLUMN `valor` `valor` DOUBLE NOT NULL ,
CHANGE COLUMN `datacad` `datacad` DATE NOT NULL ,
CHANGE COLUMN `ativo` `ativo` INT(11) NOT NULL ;
COMMIT;

ALTER TABLE `bibliotec`.`multa` 
ADD COLUMN `codemprestimo` INT NOT NULL AFTER `codusuario`,
ADD COLUMN `codcotacao` INT NOT NULL AFTER `codemprestimo`,
ADD COLUMN `diasatraso` INT NOT NULL AFTER `dataalt`,
ADD INDEX `mul_emp_idx` (`codemprestimo` ASC) VISIBLE,
ADD INDEX `mul_cot_idx` (`codcotacao` ASC) VISIBLE;
COMMIT;

ALTER TABLE `bibliotec`.`multa` 
ADD CONSTRAINT `mul_emp`
  FOREIGN KEY (`codemprestimo`)
  REFERENCES `bibliotec`.`emprestimo` (`codemprestimo`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `mul_cot`
  FOREIGN KEY (`codcotacao`)
  REFERENCES `bibliotec`.`cotacao` (`codcotacao`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
COMMIT;  

ALTER TABLE `bibliotec`.`cotacao` 
CHANGE COLUMN `datacad` `dataini` DATE NOT NULL ,
CHANGE COLUMN `dataalt` `datafim` DATE NULL DEFAULT NULL ;
COMMIT;

ALTER TABLE `bibliotec`.`emprestimo` 
ADD COLUMN `dataentr` DATE NULL AFTER `datadev`;
COMMIT;

ALTER TABLE `bibliotec`.`multa` 
CHANGE COLUMN `dataalt` `datapag` DATE NULL DEFAULT NULL ;
COMMIT;

ALTER TABLE `bibliotec`.`usuarios` 
ADD COLUMN `bloq` INT NULL AFTER `jaativado`,
ADD COLUMN `datahorabloq` TIMESTAMP NULL AFTER `bloq`;

COMMIT;

USE `bibliotec`;
COMMIT;

