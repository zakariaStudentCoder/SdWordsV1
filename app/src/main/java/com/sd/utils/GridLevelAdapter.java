package com.sd.utils;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sd.db.SudokuTableObject;
import com.sd.games.SudokuGame;
import com.sd.v1.R;

import java.util.ArrayList;

/**
 * Created by Admin on 31.03.2016.
 */
public class GridLevelAdapter extends ArrayAdapter<SudokuTableObject> {

    Context mContext;
    int resourceId;
    int mLevel;
    ArrayList<SudokuTableObject> data = new ArrayList<SudokuTableObject>();

    public GridLevelAdapter(Context context ,int level , ArrayList<SudokuTableObject> data)
    {
        super(context, 0 , data);
        this.mContext = context;
        this.data = data;
        this.mLevel = level;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View itemView = convertView;
        ViewHolder holder = null;
        int imgResource = 0;

        SudokuTableObject item = getItem(position);

        final LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemView = (item.getState() == SudokuGame.GAME_STATE_PLAYING)
                ? LayoutInflater.from(getContext()).inflate(R.layout.level1_grid_item,parent, false)
                : LayoutInflater.from(getContext()).inflate(R.layout.level1_grid_item_closed, parent, false);

        holder = new ViewHolder();
        holder.txtItem = (TextView) itemView.findViewById(R.id.textId);
        holder.imgItem = (ImageView)itemView.findViewById(R.id.imgLevel);
        holder.id = new Integer(position);
        itemView.setTag(holder);


        if((item.getState() == SudokuGame.GAME_STATE_PLAYING)) {

            switch (mLevel)
            {
                case 1 :
                {
                    imgResource = R.mipmap.ic_level_1_white;
                }
                break;
                case 2 :
                {
                    imgResource = R.mipmap.ic_level_2_white;

                }
                break;
                case 3 :
                {
                    imgResource = R.mipmap.ic_level_3_white;
                }
                break;

            }

            holder.txtItem.setText(item != null ? (item.getId() + "") : "Empty");
            holder.imgItem.setImageResource(imgResource);
        }

        return itemView;
    }

    public class ViewHolder
    {
        public  ImageView imgItem;
        public TextView txtItem;
        public int id;
    }
}
