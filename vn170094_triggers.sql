CREATE TRIGGER TR_TransportOffer_Prihvaceno
	ON Ponuda
	FOR UPDATE
	AS
	BEGIN
		declare @kursor CURSOR
		declare @idZahteva int
		declare @accepted int

		set @kursor = CURSOR FOR
		select idZahteva , Accepted
		FROM inserted

		open @kursor

		fetch next from @kursor
		into @idZahteva, @accepted

		while @@FETCH_STATUS = 0
		begin
			if @accepted = 1 
			begin
				delete from Ponuda where idZahteva = @idZahteva
			end

			fetch next from @kursor
			into @idZahteva, @accepted
		end

		close @kursor
		deallocate @kursor

	END