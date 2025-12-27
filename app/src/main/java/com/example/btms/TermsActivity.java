package com.example.btms;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        TextView tvTerms = findViewById(R.id.tvTerms);
        if (tvTerms != null) {
            tvTerms.setText(getTermsContent());
        }

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    private String getTermsContent() {
        return "ĐIỀU KHOẢN ĐẶT VÉ TRỰC TUYẾN\n\n" +
                "I. ĐIỀU KHOẢN VÀ QUY ĐỊNH CHUNG\n\n" +
                "Điều 1. Giải thích từ ngữ và từ viết tắt\n\n" +
                "1. \"Biểu phí\": là các loại phí thuế theo quy định của Hãng vận tải và Nhà chức trách;\n\n" +
                "2. \"Bến xe ô tô khách\": là công trình thuộc kết cấu hạ tầng giao thông đường bộ thực hiện chức năng phục vụ xe ô tô đón, trả hành khách và các dịch vụ hỗ trợ vận tải hành khách;\n\n" +
                "3. \"Chúng tôi\": là Công Ty Cổ Phần Xe Khách EASYBUS;\n\n" +
                "4. \"Điểm đón/trả\": là điểm khởi hành và điểm đến theo lịch trong hành trình của khách hàng;\n\n" +
                "5. \"Điều kiện bất khả kháng\": là sự kiện xảy ra mang tính khách quan và nằm ngoài tầm kiểm soát của các bên bao gồm, nhưng không giới hạn đến động đất, bão, lũ lụt, lốc, sóng thần, lở đất, hỏa hoạn, chiến tranh hoặc có nguy cơ xảy ra chiến tranh, bạo động, nổi loạn, đình công… và các thảm họa khác chưa lường hết được, sự thay đổi chính sách hoặc ngăn cấm của cơ quan có thẩm quyền của Việt Nam;\n\n" +
                "6. \"Điều kiện vận chuyển\": là các yêu cầu, nội dung của EASYBUS thông báo đến hành khách liên quan đến các dịch vụ vận chuyển, bao gồm các thông tin được thể hiện trên vé/phiếu xác nhận hành trình và/hoặc trên website, ứng dụng và/hoặc trong điều khoản sử dụng đã được phê duyệt và/hoặc các hình thức công bố khác;\n\n" +
                "7. \"Hành khách\": là bất kỳ cá nhân nào sử dụng dịch vụ của EASYBUS;\n\n" +
                "8. \"Hành lý\": là những vật phẩm, đồ dùng tư trang và tài sản cá nhân của hành khách mang theo, sử dụng trong chuyến đi của mình, trừ khi được quy định khác đi;\n\n" +
                "9. \"Hợp đồng vận chuyển\": là các thỏa thuận của Hãng vận chuyển và hành khách trong việc cung cấp các dịch vụ theo nhu cầu của hành khách được thể hiện bằng vé hoặc hình thức khác có giá trị tương đương với vé;\n\n" +
                "10. \"Hóa đơn điện tử\": hóa đơn có mã hoặc không có mã của cơ quan thuế được thể hiện ở dạng dữ liệu điện tử do EASYBUS cung cấp dịch vụ, lập bằng phương tiện điện tử để ghi nhận thông tin, cung cấp dịch vụ theo quy định của pháp luật về kế toán, pháp luật về thuế;\n\n" +
                "11. \"Mã tra cứu\": là mã số bao gồm 9 ký tự được hiển thị trong thông tin mua vé, dùng cho việc tra cứu hóa đơn điện tử sau khi khách hàng thanh toán thành công;\n\n" +
                "12. \"Mã đặt vé\": là các thông tin chi tiết của Hành khách đã được nhập vào hệ thống đặt giữ chỗ của Chúng tôi thông qua website/hoặc ứng dụng EASYBUS, Phòng vé, Đại lý, Tổng đài;\n\n" +
                "13. \"Ngày\": có nghĩa là các ngày dương lịch, bao gồm bảy (07) ngày trong tuần; với điều kiện là khi dùng trong việc gửi thông báo thì ngày gửi thông báo đi không được tính và khi dùng cho mục đích xác định;\n\n" +
                "14. \"Thông tin cá nhân\": Hành khách chấp nhận rằng thông tin cá nhân của hành khách được chuyển cho EASYBUS vì mục đích sử dụng dịch vụ do EASYBUS cung cấp. Vì mục đích trên, hành khách đồng ý cho phép EASYBUS lưu trữ và sử dụng các thông tin cá nhân và chuyển tải các thông tin đó tới hãng vận chuyển, các đại diện ủy quyền phát hành vé, cơ quan nhà nước có thẩm quyền, hoặc các nhà cung cấp dịch vụ liên quan khác;\n\n" +
                "15. \"Thẻ lên xe (vé)\": là bằng chứng xác nhận hợp đồng vận chuyển hành khách và EASYBUS. Có giá trị đối với hành khách có tên và hành trình được ghi rõ trong vé. Vé có thể được chuyển, hủy theo quy định của EASYBUS.\n\n" +
                "Điều 2. Quy định đặt vé trực tuyến\n\n" +
                "1. Phạm vi áp dụng: Chương trình thanh toán online được áp dụng cho các chuyến xe nhất định của EASYBUS. Thành viên của EASYBUS cũng như khách vãng lai thực hiện được hình thức thanh toán này. Việc đăng ký tham gia Thành viên EASYBUS là hoàn toàn miễn phí.\n\n" +
                "2. Đặt chỗ\n\n" +
                "a) Quý khách kiểm tra cẩn thận các thông tin vé trước khi tiến hành xác nhận đặt vé và thanh toán. Bằng việc thanh toán qua website này, Quý khách chấp nhận giờ khởi hành, vị trí ghế ngồi v.v mà Quý khách đã đặt. Quý khách đồng ý rằng, trong những trường hợp có sự thay đổi về chuyến đi hoặc bất khả kháng, chúng tôi có quyền hoàn trả lại bất kỳ vé nào từ việc mua bán qua website này hoặc thực hiện việc chuyển vé cho Quý khách qua chuyến khác theo yêu cầu của Quý khách trong trường hợp chúng tôi còn chỗ;\n\n" +
                "b) Đặt chỗ chỉ được xác nhận sau khi việc thanh toán tiền vé đã hoàn tất đồng thời EASYBUS cung cấp cho Hành khách Mã đặt vé xác định tên Hành khách, hành trình, giờ khởi hành, số ghế, giá vé;\n\n" +
                "c) Chúng tôi sẽ không chịu trách nhiệm về bất kỳ tổn thất nào mà Hành khách có thể phải chịu từ việc đặt chỗ thông qua bất kỳ tổ chức/cá nhân nào không phải là Chúng tôi hoặc Bên thứ ba được ủy quyền của Chúng tôi.\n\n" +
                "3. Xác nhận thanh toán: Sau khi hoàn thành việc thanh toán vé trực tuyến, Quý khách sẽ nhận được thư xác nhận thông tin chi tiết vé đã đặt thông qua địa chỉ thư điện tử (email) mà Quý khách đã cung cấp. Đồng thời, chúng tôi cũng sẽ gửi tin nhắn (SMS) hoặc ZNS qua Zalo thông báo mã giao dịch tới số điện thoại Quý khách đăng ký.\n\n" +
                "Lưu ý:\n\n" +
                "a) Chúng tôi không chịu trách nhiệm trong trường hợp Quý khách nhập sai địa chỉ email, số điện thoại và thông tin cá nhân khác dẫn đến không nhận được thư xác nhận. Vì vậy Quý khách vui lòng kiểm tra lại chính xác các thông tin trước khi thực hiện thanh toán. Với email, SMS và ZNS chỉ có tính chất xác nhận thông tin vé sau khi Quý khách đã đặt vé thành công;\n\n" +
                "b) Chúng tôi đề nghị Quý khách đọc kỹ các thông tin về chuyến đi, giờ khởi hành và chỗ ngồi v.v. trước khi hoàn tất việc xác nhận tất cả các thông tin về vé;\n\n" +
                "c) Email xác nhận thông tin đặt vé có thể đi vào hộp thư rác (spam mail) của Quý khách, vì vậy hãy kiểm tra trước khi liên lạc với chúng tôi;\n\n" +
                "d) Sau 30 phút kể từ khi Quý khách thanh toán thành công mà vẫn chưa nhận được bất kỳ xác nhận nào (qua email hoặc SMS/ ZNS), Quý khách vui lòng liên hệ chúng tôi qua tổng đài 0877086400 để được hỗ trợ. Nếu Quý khách không liên hệ lại coi như EASYBUS đã hoàn thành nghĩa vụ với Quý khách.\n\n" +
                "4. Bảo đảm an toàn giao dịch\n\n" +
                "a) Quản lý thông tin nhà cung cấp dịch vụ: EASYBUS (hoặc bên thứ ba - nhà cung cấp cổng thanh toán điện tử, hoặc/và các bên ký kết khác) sẽ sử dụng các công nghệ đặc biệt để nhận biết các hoạt động giả mạo trên trang mạng như: sử dụng thẻ tín dụng giả v.v. Sự chấp nhận hợp tác của Quý khách cùng với nỗ lực của EASYBUS là rất cần thiết. Quý khách chấp nhận rằng EASYBUS có thể chấm dứt quyền truy cập và sử dụng trang mạng của EASYBUS nếu Quý khách hoặc người khác hành động nhân danh Quý khách vì lý do nào đó nằm trong diện nghi vấn có gian lận hoặc vi phạm các điều khoản này;\n\n" +
                "b) Kiểm soát giao dịch và thông tin phản hồi khách hàng: EASYBUS sẽ hết sức cố gắng sử dụng mọi biện pháp và tuân theo mọi cách thức có thể để giữ an toàn cho tất cả các thông tin cá nhân của Quý khách, và chúng tôi cũng sẽ thường xuyên cập nhật những thông tin chính xác nhất. Website này có những công nghệ an ninh đảm bảo việc bảo vệ các thông tin bị thất lạc, lạm dụng hoặc thay đổi. Tất cả các giao dịch và thông tin về thẻ được sử dụng đều được đảm bảo an toàn cho các giao dịch kinh tế ngày nay. Mặc dù vậy, không phải tất cả các dữ liệu truyền qua Internet đều có thể đảm bảo 100%, vì thế chúng tôi không thể đưa ra một sự đảm bảo tuyệt đối rằng mọi thông tin Quý khách cung cấp đều được bảo vệ tất cả mọi lúc.\n\n" +
                "5. Thông tin cá nhân\n\n" +
                "a) Thông tin cá nhân của Quý khách mà chúng tôi có được trong quá trình giao dịch chỉ dùng vào các mục đích sau:\n\n" +
                "- Hỗ trợ và giải đáp các thắc mắc của Quý khách;\n\n" +
                "- Cập nhật các thông tin mới nhất về các chương trình, dịch vụ v.v. của EASYBUS đến Quý khách.\n\n" +
                "b) Chúng tôi thu thập và sử dụng thông tin cá nhân của Quý khách phù hợp với mục đích đã nêu bên trên và hoàn toàn tuân thủ nội dung của \"Chính sách bảo mật\". Chúng tôi cam kết chỉ sử dụng cho mục đích và phạm vi đã nêu và không tiết lộ cho bất kỳ bên thứ ba nào khi chưa có sự đồng ý bằng văn bản của Quý khách;\n\n" +
                "c) Xin lưu ý chúng tôi được quyền cung cấp thông tin cá nhân của Quý khách trong trường hợp khi có yêu cầu từ các cơ quan Nhà nước có thẩm quyền.\n\n" +
                "6. Chính sách hoàn/hủy/đổi vé\n\n" +
                "a) Quy định hoàn trả tiền mua vé Online do lỗi giao dịch\n\n" +
                "- Các trường hợp hoàn trả tiền mua vé online cho khách do lỗi giao dịch:\n\n" +
                "- Khách hàng mua vé online giao dịch không thành công (lỗi giao dịch) chưa có Mã đặt vé (code) nhưng đã bị trừ tiền;\n\n" +
                "- Hiện nay, có một số Thẻ ATM của khách hàng (Thẻ ATM cũ được làm từ 3-4 năm trước) chỉ thực hiện được hình thức chuyển khoản không có chức năng thanh toán trực tuyến nên khi khách hàng thực hiện giao dịch chuyển khoản vào cuối tuần hoặc vào ngày Lễ, Tết, hệ thống ngân hàng không xác nhận tiền trong tài khoản của EASYBUS nên khách hàng phải thanh toán trực tiếp tại quầy vé (Khách hàng vừa bị trừ tiền trong tài khoản vừa phải ra quầy vé thanh toán tiền mặt lấy vé).\n\n" +
                "b) Thời gian hoàn trả tiền cho khách hàng\n\n" +
                "- Đối với Bộ phận Tổng đài: Thời gian hoàn trả tiền tới tài khoản khách hàng là từ 03 ngày đến 05 ngày làm việc kể từ khi Ban Tài chính – Kế toán nhận chứng từ thanh toán;\n\n" +
                "- Đối với các quầy vé: Giao dịch trực tiếp với khách hàng và hoàn trả ngay thời điểm giao dịch.\n\n" +
                "- Đối với hoàn trả tiền mua vé qua App: Thời gian theo chính sách của từng nhà phát triển App\n\n" +
                "c) Quy định thay đổi hoặc hủy vé\n\n" +
                "- Chỉ được chuyển đổi vé 1 lần duy nhất\n\n" +
                "- Chi phí hủy vé từ 10% – 30% giá vé tùy thuộc thời gian hủy vé so với giờ khởi hành ghi trên vé và số lượng vé cá nhân/tập thể áp dụng theo các quy định hiện hành.\n\n" +
                "- Quý khách khi có nhu cầu muốn thay đổi hoặc hủy vé đã thanh toán, cần liên hệ với Trung tâm tổng đài 0877086400 hoặc quầy vé chậm nhất trước 24h so với giờ xe khởi hành được ghi trên vé, trên email hoặc tin nhắn để được hướng dẫn thêm.\n\n" +
                "7. Kênh bán vé\n\n" +
                "a) EASYBUS khuyến cáo Quý khách mua vé lựa chọn một trong các phương thức mua vé bao gồm mua trực tiếp tại website, app, phòng vé chính thức hoặc mua vé qua Tổng đài 0877086400 để đảm bảo không mua phải vé giả, vé bị nâng giá v.v.;\n\n" +
                "b) Nếu phát hiện ra Quý khách gian lận, vi phạm điều khoản sử dụng, có hành vi đầu cơ, mua đi bán lại, bán vé chợ đen. EASYBUS có quyền từ chối cung cấp dịch vụ và không giải quyết các vấn đề phát sinh nếu Quý khách mua vé từ các kênh không thuộc hệ thống bán vé của EASYBUS.\n\n" +
                "8. Trung chuyển: Nếu quý khách có nhu cầu trung chuyển, vui lòng liên hệ số điện thoại 0877086400 trước khi đặt vé. Chúng tôi sẽ không đón/trung chuyển tại những địa điểm xe trung chuyển không thể đến được.\n\n" +
                "Điều 3: Quy định vận chuyển\n\n" +
                "1. Trẻ em dưới 6 tuổi và phụ nữ có thai\n\n" +
                "a) Trẻ em dưới 6 tuổi, cao từ 1.3m trở xuống, cân nặng dưới 30kg thì không phải mua vé.\n\n" +
                "b) Phụ nữ có thai cần đảm bảo sức khoẻ trong suốt quá trình di chuyển.\n\n" +
                "2. Hành lý\n\n" +
                "a) Tổng trọng lượng hành lý không vượt quá 20kg;\n\n" +
                "b) Đối với hành lý quá khổ, cồng kềnh vui lòng liên hệ Tổng đài 0877086400\n\n" +
                "3. Yêu cầu khi lên xe\n\n" +
                "a) Có mặt trước giờ xe khởi hành 30 phút tại Bến đi (đối với ngày lễ tết cần ra trước 60 phút)\n\n" +
                "b) Xuất trình thông tin vé được gửi qua SMS/Email/EASYBUS App hoặc liên hệ quầy vé để nhận thông tin vé trước khi lên xe.\n\n" +
                "c) Không mang đồ ăn, thức ăn có mùi lên xe;\n\n" +
                "d) Không hút thuốc, uống rượu, sử dụng chất kích thích trên xe;\n\n" +
                "e) Không mang các vật dễ cháy nổ lên xe;\n\n" +
                "f) Không vứt rác trên xe;\n\n" +
                "g) Không mang động vật lên xe.\n\n" +
                "II. CHÍNH SÁCH BẢO MẬT\n\n" +
                "Điều 1. Quy định chung\n\n" +
                "1. Nhằm mang lại trải nghiệm tốt nhất cho người dùng trên website của EASYBUS, thông tin nhận dạng cá nhân hoặc dữ liệu cá nhân của quý khách hàng sẽ được thu thập, sử dụng, tiết lộ, xử lý trong khuôn khổ bảo vệ người dùng;\n\n" +
                "2. Sau khi đọc Chính sách bảo mật này, quý khách hàng tự quyết định việc chia sẻ dữ liệu cá nhân với chúng tôi. Dữ liệu cá nhân ở đây đề cập đến mọi thông tin liên quan đến một cá nhân có thể định danh/ nhận dạng được. Khi nhấp chọn \"đồng ý\", quý khách hàng thừa nhận rằng quý khách hàng đã đọc, đồng ý và chấp thuận cho chúng tôi thu thập, sử dụng và xử lý Dữ liệu cá nhân theo Chính sách bảo mật này và/hoặc các Điều khoản sử dụng. Đồng thời, quý khách hàng cũng thừa nhận rằng toàn bộ Dữ liệu cá nhân mà quý khách hàng đã cung cấp hoặc sẽ cung cấp là dữ liệu chính chủ, đúng và chính xác.\n\n" +
                "3. Tùy từng thời điểm EASYBUS có thể sửa đổi Chính sách bảo mật này để phản ánh các thay đổi về luật pháp và quy định, thông lệ sử dụng của EASYBUS, các tính năng Hệ thống và/hoặc các tiến bộ công nghệ. Chúng tôi khuyến khích khách hàng thường xuyên xem lại Chính sách Bảo mật thông tin cá nhân trên EASYBUS. Cập nhật thông tin liên tục có thể đảm bảo bạn biết và thực hiện tốt quản lý cá nhân.\n\n" +
                "Điều 2. Chính sách bảo mật\n\n" +
                "1. Thông tin thu thập: Khi được sự đồng ý của quý khách hàng, chúng tôi có thể thu thập Dữ liệu cá nhân của quý khách hàng để cung cấp dịch vụ của chúng tôi cho quý khách hàng khi sử dụng Hệ thống dữ liệu EASYBUS. Dữ liệu cá nhân bao gồm những thông tin được trình bày như sau:\n\n" +
                "a) Thông tin cá nhân cơ bản: khi quý khách hàng đang sử dụng Hệ thống của chúng tôi, chúng tôi có thể yêu cầu quý khách hàng tạo một tài khoản để tiến hành đặt chỗ. Dữ liệu cá nhân được thu thập sẽ bao gồm, nhưng không giới hạn tên của quý khách hàng, thông tin nhận dạng người dùng và thông tin đăng nhập của EASYBUS ID, Địa chỉ thư điện tử (email), số điện thoại, địa chỉ và phương thức thanh toán;\n\n" +
                "b) Thông tin cá nhân cụ thể: chúng tôi có thể thu thập Dữ liệu cá nhân của quý khách hàng dựa trên quá trình quý khách hàng sử dụng Hệ thống của chúng tôi, ví dụ: chi tiết về quyền thành viên thường xuyên cũng như những đánh giá của quý khách hàng. Chúng tôi cũng có thể thu thập một số thông tin nhất định từ quý khách hàng khi quý khách hàng đang sử dụng Hệ thống của chúng tôi, chẳng hạn như vị trí địa lý, địa chỉ IP, tùy chọn tìm kiếm cũng như các dữ liệu liên quan đến việc sử dụng Internet chung khác;\n\n" +
                "c) Vị trí địa lý: khi được sự đồng ý của quý khách hàng, chúng tôi có thể thu thập Dữ liệu cá nhân của quý khách hàng về vị trí thực tế của quý khách hàng để cung cấp cho quý khách hàng các ưu đãi ở các vị trí liên quan được cung cấp trên Hệ thống của chúng tôi. Chúng tôi cũng có thể lấy được vị trí gần đúng của quý khách hàng từ địa chỉ IP và GPS của quý khách hàng;\n\n" +
                "d) Thông tin nhật ký: khi quý khách hàng đang sử dụng Hệ thống của chúng tôi, chúng tôi có thể thu thập dữ liệu của quý khách hàng mà chúng tôi gọi là \"thông tin nhật ký\". Thông tin nhật ký vẫn có thể được thu thập ngay cả khi quý khách hàng không tạo bất kỳ tài khoản nào trên Hệ thống của chúng tôi. Thông tin nhật ký này có thể bao gồm địa chỉ IP, loại trình duyệt, hệ điều hành, quốc tịch, vị trí truy cập trang, nhà cung cấp dịch vụ di động, thông tin thiết bị và lịch sử tìm kiếm cũng như thông tin liên quan đến việc sử dụng internet chung khác. Chúng tôi sử dụng thông tin nhật ký để cung cấp cho người dùng trải nghiệm tốt hơn khi sử dụng Hệ thống của chúng tôi;\n\n" +
                "e) Cookies và các công nghệ tương tự: chúng tôi có thể sử dụng cookie và/hoặc các công nghệ tương tự (như tập tin chỉ báo-web beacons, thẻ-tags, tập lệnh-scripts). Cookies là một phần nhỏ dữ liệu được lưu trữ trong máy tính hoặc thiết bị di động của quý khách hàng để giúp chúng tôi theo dõi quý khách hàng. Giống như các trang web khác, chúng tôi có thể sử dụng cookies để cung cấp cho quý khách hàng trải nghiệm tốt hơn, vì vậy máy tính và/hoặc thiết bị di động của quý khách hàng sẽ nhận ra quý khách hàng khi quý khách hàng sử dụng Hệ thống của Chúng tôi sau đó. Vui lòng hủy tùy chọn cookies, nếu quý khách hàng muốn chúng tôi dừng tính năng cookies.\n\n" +
                "2. Mục đích sử dụng thông tin: Chúng tôi sẽ nhận thông tin dữ liệu cá nhân khi khách hàng cài đặt và sử dụng. Khi được sự đồng ý của quý khách hàng, Chúng tôi có thể sử dụng thông tin của quý khách hàng được thu thập thông qua Hệ thống cho các mục đích sau:\n\n" +
                "a) Đăng ký sử dụng và/hoặc truy cập hệ thống;\n\n" +
                "b) Quản lý, vận hành, quản trị và/hoặc truy cập hệ thống;\n\n" +
                "c) Liên hệ với quý khách hàng về các vấn đề liên quan đến việc quý khách hàng sử dụng và/hoặc truy cập vào Hệ thống và quản lý các truy vấn và/hoặc yêu cầu do quý khách hàng gửi qua Hệ thống;\n\n" +
                "d) Tùy chỉnh trải nghiệm của quý khách hàng khi sử dụng hệ thống và cải thiện trải nghiệm và sự hài lòng của khách hàng;\n\n" +
                "e) Thực thi các quy định trong các Điều khoản và Điều kiện của chúng tôi;\n\n" +
                "f) Giải quyết tranh chấp, thu tiền thanh toán còn tồn đọng và xử lý sự cố và/hoặc cho các mục đích về tiếp thị như:\n\n" +
                "- Tiếp thị truyền thống trong đó, bao gồm nhưng không giới hạn, gửi email cho quý khách hàng về các sản phẩm mới, khuyến mại đặc biệt và các cuộc khảo sát hoặc các thông tin khác mà chúng tôi nghĩ quý khách hàng có thể thấy thú vị;\n\n" +
                "- Tiếp thị kỹ thuật số bao gồm, nhưng không giới hạn truyền thông xã hội, quảng cáo hiển thị, tối ưu hóa công cụ tìm kiếm (\"SEO\"), tiếp thị qua công cụ tìm kiếm (\"SEM\"), thông báo đẩy (Push Notification) bằng cách sử dụng các kỹ thuật đồ thị mở.\n\n" +
                "3. Chia sẻ Dữ liệu cá nhân: Tùy thuộc vào từng trường hợp cụ thể phải cung cấp thông tin cho những người hoặc các tổ chức có thể được tiếp cận, EASYBUS có thể tiết lộ Dữ liệu cá nhân của quý khách hàng với các điều kiện sau:\n\n" +
                "a) Cung cấp thông tin khi có sự chấp thuận: Chúng tôi chỉ chia sẻ Dữ liệu cá nhân của quý khách hàng với Bên thứ ba khi Chúng tôi nhận được sự chấp thuận của quý khách hàng cho phép Chúng tôi làm như vậy. Chúng tôi sẽ cần sự chấp thuận của quý khách hàng để chia sẻ bất kỳ Dữ liệu cá nhân nhạy cảm nào, theo yêu cầu của luật pháp và quy định hiện hành. Khi nhấp chọn nút \"Đồng ý\", quý khách hàng đã thiết lập một hành động khẳng định rõ ràng và một thỏa thuận tự nguyện, cụ thể, đã hiểu rõ và không mơ hồ về việc xử lý Dữ liệu cá nhân. Điều này có thể bao gồm cả việc chia sẻ Dữ liệu cá nhân đã thu thập cho Bên thứ ba;\n\n" +
                "b) Cung cấp thông tin vì lý do pháp lý: Chúng tôi có thể có toàn quyền quyết định về việc chia sẻ Dữ liệu cá nhân của quý khách hàng với Bên thứ ba nếu chúng tôi cho rằng việc chia sẻ dữ liệu là cần thiết để:\n\n" +
                "- Tuân thủ luật pháp và quy định hiện hành;\n\n" +
                "- Thực thi các Điều khoản và Điều kiện của Chúng tôi;\n\n" +
                "- Điều tra bất kỳ hành vi gian lận hoặc hành vi bất hợp pháp nào;\n\n" +
                "- Bảo vệ thương hiệu, uy tín cũng như tài sản của Chúng tôi.\n\n" +
                "4. Bảo mật dữ liệu cá nhân\n\n" +
                "a) Cam kết bảo mật:\n\n" +
                "- Chúng tôi nỗ lực đảm bảo cung cấp thông tin có trách nhiệm và hệ thống hoạt động chính xác;\n\n" +
                "- EASYBUS mong muốn mang lại cảm giác an toàn cho khách hàng khi sử dụng dịch vụ mua vé xe online. Chúng tôi cam kết bảo vệ tất cả thông tin mà Chúng tôi nhận được từ khách hàng. Để ngăn chặn truy cập trái phép, đảm bảo sử dụng đúng thông tin, Chúng tôi sẽ sử dụng các phương pháp và công nghệ bảo mật Internet hợp lý.\n\n" +
                "b) An toàn dữ liệu: Chúng tôi đã và đang thực hiện nhiều biện pháp an toàn, bao gồm:\n\n" +
                "- Chúng tôi lưu trữ không tin cá nhân khách hàng trong môi trường vận hành an toàn. Chỉ có nhân viên, đại diện và nhà cung cấp mới có thể truy cập khi cần phải biết;\n\n" +
                "- Chúng tôi tuân theo các tiêu chuẩn ngành, pháp luật trong việc bảo mật thông tin cá nhân khách hàng.\n\n" +
                "5. Lưu trữ Dữ liệu cá nhân\n\n" +
                "a) Miễn là thông tin của quý khách hàng vẫn còn tồn tại, chúng tôi sẽ lưu Dữ liệu cá nhân của quý khách hàng để cung cấp các dịch vụ khi cần. Chúng tôi sẽ ngừng lưu trữ Dữ liệu cá nhân hoặc với nỗ lực hợp lý để xóa các phương tiện có liên quan đến Dữ liệu cá nhân của Quý khách hàng, ngay khi:\n\n" +
                "- Mục đích thu thập Dữ liệu cá nhân không còn phù hợp với việc lưu trữ dữ liệu;\n\n" +
                "- Khách hàng yêu cầu hủy bỏ.\n\n" +
                "b) Thời gian lưu trữ dữ liệu bắt đầu từ khi EASYBUS nhận được yêu cầu lưu trữ dữ liệu đến khi kết thúc yêu cầu. Thời gian lưu trữ tối thiểu là 24 tháng.\n\n" +
                "6. Quyền của khách hàng đối với Dữ liệu cá nhân: Quý khách hàng có quyền cập nhật, thay đổi hoặc hủy bỏ Dữ liệu cá nhân bất kỳ lúc nào. Trong các tình huống cần lưu ý dưới đây:\n\n" +
                "a) Phương thức truy cập hoặc chỉnh sửa Dữ liệu cá nhân: Khi quý khách hàng cung cấp cho Chúng tôi Dữ liệu cá nhân của quý khách hàng, vui lòng đảm bảo rằng những dữ liệu đó là chính xác và đầy đủ. Nếu quý khách hàng tin rằng bất kỳ thông tin nào mà Chúng tôi đang nắm giữ có sai sót hoặc thiếu sót, vui lòng đăng nhập vào tài khoản của quý khách hàng trên Hệ thống và sửa lại thông tin. Ngoài ra, quý khách hàng nên nhanh chóng cập nhật Dữ liệu cá nhân thông qua tài khoản nếu có bất kỳ thay đổi nào;\n\n" +
                "b) Rút lại sự chấp thuận: Quý khách hàng có thể rút lại sự chấp thuận đối với việc thu thập, sử dụng hoặc tiết lộ Dữ liệu cá nhân của Chúng tôi bằng cách gửi thông báo hợp lý cho Chúng tôi theo thông tin liên hệ trình bày bên dưới. Theo yêu cầu của quý khách hàng, Chúng tôi sẽ ngừng thu thập, sử dụng hoặc tiết lộ Dữ liệu cá nhân của quý khách hàng, trừ khi pháp luật yêu cầu hoặc nếu Chúng tôi có các mục đích kinh doanh hoặc pháp lý hợp pháp để giữ lại dữ liệu đó;\n\n" +
                "Lưu ý: khi Quý khách hàng rút lại sự đồng thuận đối với việc thu thập, sử dụng hoặc tiết lộ Dữ liệu cá nhân sẽ khiến Chúng tôi không thể tiếp tục cung cấp cho quý khách hàng các dịch vụ của Chúng tôi và quý khách hàng đồng ý rằng Chúng tôi sẽ không chịu trách nhiệm với quý khách hàng về bất kỳ tổn thất hoặc thiệt hại nào phát sinh từ hoặc liên quan đến việc chấm dứt dịch vụ như vậy.\n\n" +
                "c) Xóa Dữ liệu cá nhân: Quý khách hàng có thể yêu cầu xóa Dữ liệu cá nhân của quý khách hàng do Chúng tôi thu thập và xử lý, bằng cách gửi cho Chúng tôi một lý do hợp lý và thông báo cho Chúng tôi theo thông tin liên hệ của Chúng tôi được trình bày bên dưới.\n\n" +
                "7. Đăng ký và quyền thành viên: Hệ thống này cho phép quý khách hàng tạo tài khoản người dùng dựa trên dữ liệu quý khách hàng cung cấp. Bằng cách cung cấp dữ liệu, đăng ký và tạo tài khoản của quý khách hàng, quý khách hàng đảm bảo rằng:\n\n" +
                "a) Quý khách hàng đã đủ 18 tuổi;\n\n" +
                "b) Thông tin về quý khách hàng là đúng và chính xác, ở thời điểm hiện tại và đầy đủ theo yêu cầu trong mẫu đăng ký trên Hệ thống \"Dữ liệu đăng ký\" và Quý khách hàng sẽ cập nhật Dữ liệu đăng ký này để duy trì tính chính xác và đầy đủ.\n\n" +
                "Thông tin liên hệ của EASYBUS: Nếu quý khách hàng có bất kỳ câu hỏi hoặc yêu cầu nào liên quan đến Chính sách Bảo mật này, vui lòng liên hệ với Chúng tôi qua: support@easybus.vn hoặc gọi đến số điện thoại 0877086400.\n\n" +
                "EASYBUS - Chất lượng là Danh dự";
    }
}




