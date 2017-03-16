package com.vipycm.mao.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;

import java.util.LinkedList;
import java.util.Queue;

/**
 * data structure and algorithms
 * Created by mao on 2016/12/29.
 */
public class DsFragment extends MaoFragment {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    TextView txt_content;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_sample, container, false);
        txt_content = (TextView) rootView.findViewById(R.id.txt_content);
        txt_content.setText(this.getClass().getSimpleName());
        return rootView;
    }

    @Override
    public void onDestroyView() {
        log.i("onDestroyView");
        super.onDestroyView();

    }

    @Override
    public void onMaoClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                Node root = new Node("a");
                Node b = new Node("b");
                Node c = new Node("c");
                Node d = new Node("d");
                Node e = new Node("e");
                root.left = b;
                root.right = c;
                c.left = d;
                d.right = e;
                traverse(root);
                preTraverse(root);

                int[] arr = {1, 2, 8, 5, 6, 9, 10, 55, 87, 0};
                quickSort(arr, 0, arr.length - 1);
                for (int i : arr) {
                    System.out.print(i + ",");
                }
                System.out.println();
                break;
        }
    }

    void traverse(Node root) {
        Queue<Node> layer = new LinkedList<>();
        layer.offer(root);
        while (!layer.isEmpty()) {
            Queue<Node> nextLayer = new LinkedList<>();
            Node node = layer.poll();
            while (node != null) {
                System.out.println(node.name);
                if (node.left != null) {
                    nextLayer.offer(node.left);
                }
                if (node.right != null) {
                    nextLayer.offer(node.right);
                }
                node = layer.poll();
            }
            layer = nextLayer;
        }
    }

    void preTraverse(Node root) {
        System.out.println(root.name);
        if (root.left != null) {
            preTraverse(root.left);
        }
        if (root.right != null) {
            preTraverse(root.right);
        }
    }

    class Node {

        public Node(String name) {
            this.name = name;
        }

        String name;
        Node left;
        Node right;
    }

    private void quickSort(int[] arr, int left, int right) {
        int i = left;
        int j = right;
        if (i >= j) {
            return;
        }
        int key = arr[i];
        while (i < j) {

            while (i < j && arr[j] >= key) {
                j--;
            }
            if (i < j) {
                arr[i] = arr[j];
                i++;
            }

            while (i < j && arr[i] < key) {
                i++;
            }
            if (i < j) {
                arr[j] = arr[i];
                j--;
            }
        }
        arr[i] = key;
        quickSort(arr, left, i - 1);
        quickSort(arr, i + 1, right);
    }
}
