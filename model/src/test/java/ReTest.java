import java.util.regex.Pattern;

/**
 * @author ice
 * @date 2022/8/9 21:01
 */

public class ReTest {
    public static void main(String[] args) {
        String phone = "19813323461";
        Pattern compile = Pattern.compile("^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$");
        boolean matches = compile.matcher(phone).matches();
        System.out.println(matches);
    }
}
