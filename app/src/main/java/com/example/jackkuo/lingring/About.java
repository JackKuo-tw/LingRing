package com.example.jackkuo.lingring;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class About extends Fragment {

    View V;

    public About() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        V = inflater.inflate(R.layout.fragment_about, container, false);
        TextView prologue = V.findViewById(R.id.prologue);

        String bodyData = "<br><h1>LingRing</h1>\n" +
                "<p>一款可在網路不穩定時自動切換到傳統撥號方式的網路電話應用程式，作者在開始動工此 App 時是使用預付卡，每個月除了網路費用 90 元並不希望額外付出通話費，打電話是很貴的服務，但生活中有時要聯絡廠商、訂便當等，因此若是對方有連上網的話，那我們就使用網路電話撥號，若否則立刻轉用傳統撥號方式，此 App 重點在於自動切換，節省時間。</p>\n" +        
                "<h2>特別感謝</h2>\n" +
                "<ul>\n" +
                "<li>吳老師、周老師、遠哥指點迷津</li>\n" +
                "<li>409 實驗室設備支援</li>\n" +
                "\n" +
                "</ul>\n" +
                "<h2>參與開發</h2>\n" +
                "<p>對此 App 有興趣的人歡迎聯絡我加入此專案 GitHub Repository</p>\n" +
                "<p>Email: s104321031@mail1.ncnu.edu.tw</p>\n";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            prologue.setText(Html.fromHtml(bodyData,Html.FROM_HTML_MODE_LEGACY));
            Log.d("settext", "1");
        } else {
            prologue.setText(Html.fromHtml(bodyData));
            Log.d("settext", "2");
        }

        //使可以滑動
        prologue.setMovementMethod(new ScrollingMovementMethod());
        // Inflate the layout for this fragment
        return V;
    }

}
