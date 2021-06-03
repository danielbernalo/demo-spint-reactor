package com.bernal.inditex.prices.worker.ingestion.readers;

import static com.bernal.inditex.domain.converters.Utils.parseDate;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

import com.bernal.inditex.domain.entity.Price;
import com.bernal.inditex.domain.errors.ParseDateException;
import com.bernal.inditex.prices.worker.domain.errors.MissingFilePathException;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriceCsvReader implements CsvReader<Price> {

	private String filePath;

	public PriceCsvReader(ApplicationArguments args) {
		filePath = args.getNonOptionArgs().get(0);
	}

	@Override
	public List<Price> read() throws MissingFilePathException {
		try {
			return read(filePath).stream().map(line -> createPrice(line)).collect(Collectors.toList());
		} catch (IOException e) {
			throw new MissingFilePathException();
		} catch (CsvException e) {
			log.error("Csv File Exception", e);
		}
		return new ArrayList();
	}

	private Price createPrice(String[] line) {
		try {
			return Price.builder()
				.brandId(parseLong(line[0]))
				.starDate(parseDate(line[1]))
				.endDate(parseDate(line[2]))
				.priceList(parseInt(line[3]))
				.productId(parseLong(line[4]))
				.priority(parseLong(line[5]))
				.price(Double.valueOf(line[6]))
				.currency(line[7])
				.lastUpdate(parseDate(line[8]))
				.build();
		} catch (ParseDateException e) {
			log.info("Error al tratar de parsear fecha invalida", e);
		}
		return null;
	}


}
