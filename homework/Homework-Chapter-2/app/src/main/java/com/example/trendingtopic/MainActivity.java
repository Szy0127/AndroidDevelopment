package com.example.trendingtopic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Topic> topicList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTopics();
        TopicAdapter adaper = new TopicAdapter();
        adaper.changeData(topicList);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaper);

        EditText search = (EditText)findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                //Toast.makeText(MainActivity.this,topicList.get(0).content,Toast.LENGTH_SHORT).show();
                List<Topic> filters = new ArrayList<>();
                String searchContent = String.valueOf(s);
                for (Topic item : topicList) {
                    if (item.content.contains(searchContent)) {
                        filters.add(item);
                    }
                }
                adaper.changeData(filters);
            }

        });}

    private void initTopics()
    {

        Topic topic = new Topic(1,"最高检察报告点名辣笔小球",2190475,"英烈不容诋毁、法律不容挑衅！最高人民检察院工作报告指出，网络大V“辣笔小球”恶意诋毁贬损卫国戍边英雄官兵，江苏检察机关迅速介入，依法适用今年3月1日起施行的刑法修正案（十一），首次以涉嫌侵害英雄烈士名誉、荣誉罪批准逮捕，并在军事检察机关支持配合下，开展公益诉讼调查。");
        topicList.add(topic);
        topic = new Topic(2,"浙江有20例隔离后阳性病例",1194292,"浙江今年已有20例14天隔离期后阳性病例#截至3月7日24时，正在浙江省内隔离的入境人员4206人。今年以来，全省在14天集中隔离医学观察的入境人员中累计发现16例确诊病例、29例无症状感染者。全省已报告20例14天隔离期满后发现的核酸检测阳性病例，有效阻断了隐性传染源");
        topicList.add(topic);
        topic = new Topic(3,"女神防晒美白秘籍",1128962, "VitaBloom植之璨美白防晒助力女神大胆晒！ 点击下方博文参与互动，送出100份实物奖品，女神福利快来参与！！！");
        topicList.add(topic);
        topic = new Topic(4,"建议为缩小贫富差距开征遗产税",892256,"将幼儿园教育纳入义务教育，建议为缩小贫富差距可考虑开征遗产税等措施。");
        topicList.add(topic);
        topic = new Topic(5,"张伯礼说明年开春有望摘口罩",874688,"张伯礼院士：明年开春有望摘掉口罩，现在还要坚持戴。");
        topicList.add(topic);
        topic = new Topic(6,"逃不过的一公真香定律",830581,"快来pick你心目中的宝藏一公舞台吧！排名前三的粉丝可随机获送综艺盲盒哦～");
        topicList.add(topic);
        topic = new Topic(7,"过去2年正当防卫800余人",772112,"2018年底发布“昆山反杀案”指导性案例后，2019年和2020年因正当防卫不捕不诉800余人，是之前两年的2.8倍。");
        topicList.add(topic);
        topic = new Topic(8,"当北体小哥哥接种新冠疫苗",772070,"北体小哥哥们接种疫苗，这身材太可以了吧，想去北体当宿舍阿姨了");
        topicList.add(topic);
        topic = new Topic(9,"最高检工作报告",772009,"2021年3月8日，最高人民检察院检察长张军在十三届全国人大四次会议上作工作报告。");
        topicList.add(topic);
        topic = new Topic(10,"十三届全国人大四次会议",771824,"3月8日下午，十三届全国人大四次会议第二次全体会议在京举行。");
        topicList.add(topic);
        topic = new Topic(11,"妇女节",771763,"妇女节，请向身边的每一个她，说一声：谢谢，辛苦了！");
        topicList.add(topic);
        topic = new Topic(12,"韩国女性平等情况令人羞愧",771648,"3月8日是国际妇女节，当天，韩国总统文在寅在社交媒体发文表示，在防止女性工作经历中断、让女性在更多地方拥有更多工作机会后，才能更快实现包容性恢复和发展。");
        topicList.add(topic);
        topic = new Topic(13,"锦心似玉",771584,"【锦心萃影】今晚更新！问徐令宜@钟汉良 能有几多愁，恰似弟弟咋还不和我敞开心扉的唷？得亏徐体面有罗十一娘@谭松韵seven 这一消愁剂，徐府和平大使奖一定有罗十一娘的姓名！");
        topicList.add(topic);
        topic = new Topic(14,"李佳琦直播",771507,"关+带#李佳琦直播# 话题转，抽100人每人100元现金红包 3.8节最后一场-时尚节！超多惊喜！");
        topicList.add(topic);
        topic = new Topic(15,"女性的力量值得被看见",771387,"她们奋斗在各行各业，用无私无畏展现女性风采，以担当作为贡献巾帼力量。今天#妇女节#，#女性的力量值得被看见#，致敬每一个了不起的“她”！ ");
        topicList.add(topic);
        topic = new Topic(16,"薇娅直播",771253,"吴昕来啦！今晚#薇娅女王节#70+爆款、新品首发、买1送1等你来抢!带#薇娅直播间#转赞评+关注，抽10人每人一个随机礼盒！微博直播 ");
        topicList.add(topic);
        topic = new Topic(17,"基金",771206,"【基金入门】小白必须知道的基金入门知识，都2021年了，你还不理财吗？快上车");
        topicList.add(topic);
        topic = new Topic(18,"王冰冰请老人和子女说句话",771069,"“别老担心我们”；“工作别太要强了，我挺好”……你知道吗？镜头里乐呵呵的老人们，也会害怕。他们怕跟不上时代的脚步，怕和你说不上话……");
        topicList.add(topic);
        topic = new Topic(19,"白敬亭让大家克制",771018,"白敬亭发博“再见弈秋，你好徐坦”，粉丝评论一张身材图“直接斯哈斯哈”小白回复“克制”[doge]不是，这谁能克制的住啊？[单身狗]");
        topicList.add(topic);
        topic = new Topic(20,"美图董事长回应买加密货币",711073,"区块链技术具有颠覆现有金融和科技行业的潜力，加密货币具有足够的升值空间，并且在此时通过将其部分现金储备金分配到加密货币。 越来越多的企业开始投资比特币，而且国外很多产品也已经可以通过比特币去支付，或许不久的将来国内也会这样？看看巴菲特对区块链和比特币的看法！");
        topicList.add(topic);


    }
}
