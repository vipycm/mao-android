package com.vipycm.mao.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.mao.billing.IabHelper;
import com.vipycm.mao.billing.IabHelper.IabAsyncInProgressException;
import com.vipycm.mao.billing.IabHelper.OnConsumeFinishedListener;
import com.vipycm.mao.billing.IabHelper.OnIabPurchaseFinishedListener;
import com.vipycm.mao.billing.IabHelper.QueryInventoryFinishedListener;
import com.vipycm.mao.billing.IabResult;
import com.vipycm.mao.billing.Inventory;
import com.vipycm.mao.billing.Purchase;

import java.util.ArrayList;
import java.util.List;

/**
 * BillingFragment
 * Created by mao on 2016/12/29.
 */
public class BillingFragment extends MaoFragment {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());
    private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqQy8Aoc" +
            "rbmPVRJVG3M0HjgUYfK/aibg+LqMrN4VMvQ0WfmxwnPAecJSE+hYGEbdj3xyjIq8v4Q0XP+Mdv6mzwRDAZqSlYJCeooyknIY" +
            "feL/YNUzSc3Li5MHhlCGDtJXwLaQbKo8bbmIOOTP+Y0woGOH8+AHZ9OGx+I993pP3rsT/QbmixbUmaXOpnoxuIyg4cCNEABt" +
            "LayfF+0GhcGq3dqjtK7KiLst5auh1MfTDSStddroWhbR31l88sdMepq39qhtBur4bak/HmAJKo3IZsYKSTEviZYSFqYTHKN6" +
            "CS7W8DzMvelvKm0O38l6pLjBZOik6+/zbScmUCqr4ZOt2/QIDAQAB";

    TextView txt_content;
    Spinner bill_type;
    Spinner bill_sku;

    List<String> mSkuList = new ArrayList<>();
    ArrayAdapter<String> mSkuAdapter;
    IabHelper mHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new IabHelper(getActivity(), base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    log.d("Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_billing, container, false);
        txt_content = (TextView) rootView.findViewById(R.id.txt_content);
        txt_content.setText(this.getClass().getSimpleName());
        bill_type = (Spinner) rootView.findViewById(R.id.bill_type);
        bill_sku = (Spinner) rootView.findViewById(R.id.bill_sku);
        String[] testSkus = getResources().getStringArray(R.array.bill_skus);
        for (String sku : testSkus) {
            mSkuList.add(sku);
        }
        mSkuAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mSkuList);
        mSkuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bill_sku.setAdapter(mSkuAdapter);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        log.i("onDestroyView");
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (IabAsyncInProgressException e) {
                e.printStackTrace();
            }
            mHelper = null;
        }
    }

    @Override
    public void onMaoClick(View v) {
        final String sku = (String) (bill_sku.getSelectedItem());
        String type = (String) (bill_type.getSelectedItem());
        List<String> moreItemSkus = new ArrayList<>();
        List<String> moreSubsSkus = new ArrayList<>();
        if (IabHelper.ITEM_TYPE_INAPP.equals(type)) {
            moreItemSkus.add("test_product1");
        } else {
            moreSubsSkus.add("test_vip");
            moreSubsSkus.add("test_subs_vip1");
        }
        switch (v.getId()) {
            case R.id.isBillingSupported:
                txt_content.setText(String.valueOf(mHelper.subscriptionsSupported()));
                break;
            case R.id.getSkuDetails:
                try {
                    mHelper.queryInventoryAsync(true, moreItemSkus, moreSubsSkus, new QueryInventoryFinishedListener() {

                        @Override
                        public void onQueryInventoryFinished(IabResult result, final Inventory inv) {
                            if (inv == null) {
                                showTips("no inventory");
                                return;
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSkuList.clear();
                                    for (String sku : inv.getSkus()) {
                                        mSkuList.add(sku);
                                    }
                                    mSkuAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                } catch (IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.getBuyIntent:
                mHelper.buybuybuy(getActivity(), sku, type, 1001, new OnIabPurchaseFinishedListener() {
                    @Override
                    public void onIabPurchaseFinished(IabResult result, Purchase info) {
                        log.i("onIabPurchaseFinished " + result.getMessage() + " info:" + info);
                    }
                });
                break;
            case R.id.getPurchases:
                try {
                    mHelper.queryInventoryAsync(true, moreItemSkus, moreSubsSkus, new QueryInventoryFinishedListener() {

                        @Override
                        public void onQueryInventoryFinished(IabResult result, final Inventory inv) {
                            if (inv != null) {
                                showTips("getPurchases: " + inv.getPurchaseSkus().size());
                            } else {
                                showTips("no inventory");
                            }
                        }
                    });
                } catch (IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.consumePurchase:
                try {
                    mHelper.queryInventoryAsync(true, moreItemSkus, moreSubsSkus, new QueryInventoryFinishedListener() {

                        @Override
                        public void onQueryInventoryFinished(IabResult result, final Inventory inv) {
                            if (inv == null) {
                                showTips("no inventory");
                                return;
                            }
                            Purchase purchase = inv.getPurchase(sku);
                            if (purchase != null) {
                                try {
                                    mHelper.consumeAsync(purchase, new OnConsumeFinishedListener() {

                                        @Override
                                        public void onConsumeFinished(Purchase purchase, IabResult result) {
                                            log.i("onConsumeFinished:" + purchase.getSku() + " result:" + result.isSuccess());
                                        }
                                    });
                                } catch (IabAsyncInProgressException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                showTips("purchase is empty: " + sku);
                            }
                            showTips("getPurchases: " + inv.getPurchaseSkus().size());
                        }
                    });
                } catch (IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        log.i("onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMaoActivityResult(int requestCode, int resultCode, Intent data) {
        boolean result = mHelper.handleActivityResult(requestCode, resultCode, data);
        log.i("onMaoActivityResult: " + result);
    }

    private void showTips(final String msg) {
        Activity activity = getActivity();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            txt_content.setText(msg);
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txt_content.setText(msg);
                }
            });
        }
    }
}
