import org.codehaus.groovy.classgen.ReturnAdder;
import org.springframework.aop.aspectj.RuntimeTestWalker.ThisInstanceOfResidueTestVisitor;

import groovy.time.BaseDuration.From;

enum DataType {
	NUMBER, STRING, DATE;
	
	def static from(value) {
		for (var in this.values()) {
			if (var.name().equalsIgnoreCase(value)) {
				return var;
			}
		}
			
		throw new IllegalArgumentException("$value Ÿ���� �������� �ʽ��ϴ�.");
	}
	
	static void main(args) {
		println  from("number")
	}
}
