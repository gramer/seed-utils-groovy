import lombok.AllArgsConstructor
import lombok.Getter
import lombok.Setter
import lombok.ToString


@Getter @Setter
@ToString
class ColumnMeta {
	
	String name;
	DataType dataType;
	
	public ColumnMeta(String name, DataType dataType) {
		super();
		this.name = name;
		this.dataType = dataType;
	}
	
}
