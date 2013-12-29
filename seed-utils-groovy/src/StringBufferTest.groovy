

class StringBufferTest {
	
	static void main(args) {
		def columnMeta = new ColumnMeta();
		columnMeta.setName("test");
		
		def sheetName = "Test";
		def buffer = new StringBuffer();
		buffer << "insert into $sheetName ($columnMeta.name ,";
		for (i in (1..10)) {
			buffer << "column$i, "
		}
		
		println buffer
	}
}
