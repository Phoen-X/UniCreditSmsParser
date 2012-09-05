package com.vvygulyarniy.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.vvygulyarniy.R;
import com.vvygulyarniy.activity.TransactionInfoActivity;
import com.vvygulyarniy.to.CreditTO;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 20.07.12
 * Time: 14:26
 */
public class CreditListAdapter extends BaseAdapter
{
    ArrayList<CreditTO> credits = new ArrayList<CreditTO>();
    Context mContext;

    public int getCount() {
        return credits.size();
    }

    public Object getItem(int i) {
        return credits.get(i);
    }

    public long getItemId(int i) {
        return i;
    }

    public CreditListAdapter(Context mContext, ArrayList<CreditTO> credits)
    {
        this.credits = credits;
        this.mContext = mContext;
    }

    public View getView(int i, View view, ViewGroup viewGroup)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.credit_list,viewGroup, false);

        final CreditTO credit = credits.get(i);

        TextView amount = (TextView) itemView.findViewById(R.id.amountText);
        if (credit.getType() == CreditTO.CreditType.GET)
            amount.setTextColor(Color.parseColor("#aa3333"));
        else
            amount.setTextColor(Color.parseColor("#33aa33"));
        amount.setText(String.format("%s%.2f", credit.getType().getSign(), credit.getAmount()));
        if(i > 0)
        {
            float diff =  Math.round(Math.abs(((CreditTO)getItem(i-1)).getAmountTotal() - credit.getAmountTotal()));
            if(Math.round(credit.getAmount()) != diff)
            {
                amount.setText(amount.getText().toString() + "*");
            }
        }
        TextView date = (TextView) itemView.findViewById(R.id.dateText);
        date.setText(credit.getDateStr());
        TextView totalAmnt = (TextView) itemView.findViewById(R.id.totalText);
        totalAmnt.setText(String.format("%.2f", credit.getAmountTotal()));

        itemView.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent intent = new Intent(mContext, TransactionInfoActivity.class);
                intent.putExtra("ID", credit.getId());
                mContext.startActivity(intent);
            }
        });
        return itemView;
    }
}
