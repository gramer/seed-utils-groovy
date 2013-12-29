import java.util.List;


class SeedTool {
	
	static main(args) {
		InsertStatementBuilder builder = new InsertStatementBuilder();
		builder.setTargetSheets([
			"STD_COST_CALC", 
			"STD_COST_CALC_DETAIL"
		]);
	
		List<String> result = builder.build(builder.getClass().getResourceAsStream("seed.xlsx"));
		result.each { println(it) };
	}
}
