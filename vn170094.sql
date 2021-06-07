CREATE TABLE [Administrator]
( 
	[idAdmin]            integer  NOT NULL 
)
go

CREATE TABLE [Grad]
( 
	[idG]                integer  IDENTITY  NOT NULL ,
	[Naziv]              varchar(100)  NOT NULL ,
	[PostanskiBroj]      integer  NOT NULL 
)
go

CREATE TABLE [Korisnik]
( 
	[idK]                integer  IDENTITY  NOT NULL ,
	[Ime]                varchar(100)  NOT NULL ,
	[Prezime]            varchar(100)  NOT NULL ,
	[Password]           varchar(100)  NOT NULL ,
	[BrojPoslatihPaketa] integer  NOT NULL ,
	[Username]           varchar(100)  NOT NULL 
)
go

CREATE TABLE [Kurir]
( 
	[idKurir]            integer  NOT NULL ,
	[idV]                integer  NOT NULL ,
	[BrojIspPaketa]      integer  NOT NULL ,
	[Profit]             decimal(10,3)  NOT NULL ,
	[Status]             integer  NOT NULL 
	CONSTRAINT [IzmedjuNulaIJedan]
		CHECK  ( Status BETWEEN 0 AND 1 )
)
go

CREATE TABLE [Opstina]
( 
	[idO]                integer  IDENTITY  NOT NULL ,
	[X]                  integer  NOT NULL ,
	[Y]                  integer  NOT NULL ,
	[idG]                integer  NOT NULL ,
	[Naziv]              varchar(100)  NOT NULL 
)
go

CREATE TABLE [Paket]
( 
	[Status]             integer  NOT NULL 
	CONSTRAINT [IzmedjuNulaiTri]
		CHECK  ( Status BETWEEN 0 AND 3 ),
	[Cena]               decimal(10,3)  NULL ,
	[VremePrihvatanja]   datetime  NULL ,
	[idKurir]            integer  NULL ,
	[idP]                integer  NOT NULL 
)
go

CREATE TABLE [Ponuda]
( 
	[idKurir]            integer  NOT NULL ,
	[idZahteva]          integer  NOT NULL ,
	[Procenat]           integer  NOT NULL 
	CONSTRAINT [PozitivanBroj_1911087809]
		CHECK  ( Procenat >= 0 ),
	[idPonuda]           integer  IDENTITY  NOT NULL ,
	[Accepted]           integer  NOT NULL 
)
go

CREATE TABLE [Vozi]
( 
	[idV]                integer  NOT NULL ,
	[idKurir]            integer  NOT NULL ,
	[X]                  integer  NOT NULL ,
	[Y]                  integer  NOT NULL 
)
go

CREATE TABLE [Vozilo]
( 
	[idV]                integer  IDENTITY  NOT NULL ,
	[RegBroj]            varchar(100)  NOT NULL ,
	[TipGoriva]          integer  NOT NULL 
	CONSTRAINT [IzmedjuNulaIDva]
		CHECK  ( TipGoriva BETWEEN 0 AND 2 ),
	[Potrosnja]          decimal(10,3)  NOT NULL 
)
go

CREATE TABLE [ZahtevPostaneKurir]
( 
	[idK]                integer  NOT NULL ,
	[idV]                integer  NOT NULL 
)
go

CREATE TABLE [ZahtevZaPrevozom]
( 
	[idZahteva]          integer  IDENTITY  NOT NULL ,
	[idK]                integer  NOT NULL ,
	[OpstinaOd]          integer  NOT NULL ,
	[OpstinaDo]          integer  NOT NULL ,
	[TipPaketa]          integer  NOT NULL 
	CONSTRAINT [IzmedjuNulaIDva_1811860189]
		CHECK  ( TipPaketa BETWEEN 0 AND 2 ),
	[TezinaPaketa]       decimal(10,3)  NOT NULL 
)
go

ALTER TABLE [Administrator]
	ADD CONSTRAINT [XPKAdministrator] PRIMARY KEY  CLUSTERED ([idAdmin] ASC)
go

ALTER TABLE [Grad]
	ADD CONSTRAINT [XPKGrad] PRIMARY KEY  CLUSTERED ([idG] ASC)
go

ALTER TABLE [Korisnik]
	ADD CONSTRAINT [XPKKorisnik] PRIMARY KEY  CLUSTERED ([idK] ASC)
go

ALTER TABLE [Korisnik]
	ADD CONSTRAINT [XAK1Korisnik] UNIQUE ([Username]  ASC,[idK]  ASC)
go

ALTER TABLE [Kurir]
	ADD CONSTRAINT [XPKKurir] PRIMARY KEY  CLUSTERED ([idKurir] ASC)
go

ALTER TABLE [Opstina]
	ADD CONSTRAINT [XPKOpstina] PRIMARY KEY  CLUSTERED ([idO] ASC)
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [XPKPaket] PRIMARY KEY  CLUSTERED ([idP] ASC)
go

ALTER TABLE [Ponuda]
	ADD CONSTRAINT [XPKPonuda] PRIMARY KEY  CLUSTERED ([idPonuda] ASC)
go

ALTER TABLE [Vozi]
	ADD CONSTRAINT [XPKVozi] PRIMARY KEY  CLUSTERED ([idV] ASC)
go

ALTER TABLE [Vozilo]
	ADD CONSTRAINT [XPKVozilo] PRIMARY KEY  CLUSTERED ([idV] ASC)
go

ALTER TABLE [ZahtevPostaneKurir]
	ADD CONSTRAINT [XPKZahtevPostaneKurir] PRIMARY KEY  CLUSTERED ([idK] ASC)
go

ALTER TABLE [ZahtevZaPrevozom]
	ADD CONSTRAINT [XPKZahtevZaPrevozom] PRIMARY KEY  CLUSTERED ([idZahteva] ASC)
go


ALTER TABLE [Administrator]
	ADD CONSTRAINT [R_2] FOREIGN KEY ([idAdmin]) REFERENCES [Korisnik]([idK])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Kurir]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([idKurir]) REFERENCES [Korisnik]([idK])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Kurir]
	ADD CONSTRAINT [R_10] FOREIGN KEY ([idV]) REFERENCES [Vozilo]([idV])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Opstina]
	ADD CONSTRAINT [R_1] FOREIGN KEY ([idG]) REFERENCES [Grad]([idG])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Paket]
	ADD CONSTRAINT [R_14] FOREIGN KEY ([idKurir]) REFERENCES [Kurir]([idKurir])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [R_19] FOREIGN KEY ([idP]) REFERENCES [ZahtevZaPrevozom]([idZahteva])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Ponuda]
	ADD CONSTRAINT [R_15] FOREIGN KEY ([idKurir]) REFERENCES [Kurir]([idKurir])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Ponuda]
	ADD CONSTRAINT [R_16] FOREIGN KEY ([idZahteva]) REFERENCES [ZahtevZaPrevozom]([idZahteva])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Vozi]
	ADD CONSTRAINT [R_8] FOREIGN KEY ([idV]) REFERENCES [Vozilo]([idV])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Vozi]
	ADD CONSTRAINT [R_9] FOREIGN KEY ([idKurir]) REFERENCES [Kurir]([idKurir])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [ZahtevPostaneKurir]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([idK]) REFERENCES [Korisnik]([idK])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [ZahtevPostaneKurir]
	ADD CONSTRAINT [R_17] FOREIGN KEY ([idV]) REFERENCES [Vozilo]([idV])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [ZahtevZaPrevozom]
	ADD CONSTRAINT [R_11] FOREIGN KEY ([idK]) REFERENCES [Korisnik]([idK])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [ZahtevZaPrevozom]
	ADD CONSTRAINT [R_12] FOREIGN KEY ([OpstinaOd]) REFERENCES [Opstina]([idO])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [ZahtevZaPrevozom]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([OpstinaDo]) REFERENCES [Opstina]([idO])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go