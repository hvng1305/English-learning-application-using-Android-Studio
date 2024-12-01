package com.example.appeng;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> parentList; // Danh sách mục cha
    private final HashMap<String, List<String>> childMap; // Map mục cha -> danh sách mục con

    public ExpandableListAdapter(Context context, List<String> parentList, HashMap<String, List<String>> childMap) {
        this.context = context;
        this.parentList = parentList;
        this.childMap = childMap;

        // Kiểm tra dữ liệu đầu vào
        if (parentList == null || childMap == null) {
            throw new IllegalArgumentException("parentList and childMap must not be null");
        }

        // Kiểm tra các key trong parentList có tồn tại trong childMap không
        for (String parent : parentList) {
            if (!childMap.containsKey(parent)) {
                throw new IllegalArgumentException("childMap must contain all keys in parentList");
            }
        }
    }

    @Override
    public int getGroupCount() {
        return parentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<String> childList = childMap.get(parentList.get(groupPosition));
        return childList != null ? childList.size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<String> childList = childMap.get(parentList.get(groupPosition));
        return childList != null ? childList.get(childPosition) : null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = (String) getGroup(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.group_title);
        textView.setText(title);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String title = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.child_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.child_title);
        textView.setText(title);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
