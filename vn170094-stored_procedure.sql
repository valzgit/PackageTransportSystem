CREATE PROCEDURE OdobrenjeZahteva
@username VARCHAR(100),
@Uspeo int output
AS
BEGIN
declare @idV int
declare @idK int
set @Uspeo = 0
SELECT @idK = IdK FROM Korisnik WHERE username = @username
if(ISNull(@idK,0)>0)
BEGIN
	SELECT @idV = idV FROM ZahtevPostaneKurir WHERE idK = @idK AND idV NOT IN (SELECT idV from Kurir)
	IF(ISNull(@idV,0)>0)
	BEGIN
		DELETE FROM ZahtevPostaneKurir WHERE idK = @idK
		INSERT INTO Kurir VALUES(@idK,@idV,0,0,0)
		set @Uspeo = 1
	END
END
END
GO
