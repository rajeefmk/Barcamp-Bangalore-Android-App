/*
 * Copyright (C) 2012 Saurabh Minni <http://100rabh.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bangalore.barcamp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bangalore.barcamp.data.Session;
import com.bangalore.barcamp.widgets.CircularImageView;

public class SlotItemsListAdapter extends ArrayAdapter<Session> {

	private int listViewResource;
	private LayoutInflater layoutInflaterService;

	public SlotItemsListAdapter(Context context, int listViewResource,
			List<Session> items) {
		super(context, listViewResource, items);
		this.listViewResource = listViewResource;
		this.layoutInflaterService = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		Session session = getItem(position);

		if (convertView == null) {
			convertView = layoutInflaterService.inflate(listViewResource, null);

			holder = new ViewHolder();
			holder.text1 = (TextView) convertView
					.findViewById(android.R.id.text1);
			holder.text2 = (TextView) convertView
					.findViewById(android.R.id.text2);
			holder.image = (CircularImageView) convertView
					.findViewById(R.id.imageView1);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		if (holder.text1 != null) {
			String text = session.presenter;
			holder.text1.setText(text + " @ " + session.location);
			// holder.text1
			// .setBackgroundResource(R.drawable.session_list_item_title_background_drawable);
			holder.text1.setBackground(new MyDrawable(session.color));
			// holder.text1.setBackgroundColor(Color.parseColor(session.color));
			holder.text1.invalidate();
			// holder.text1.getBackground().setColorFilter(
			// Color.parseColor(session.color), PorterDuff.Mode.SCREEN);
		}
		if (holder.text2 != null) {
			holder.text2.setText(session.title + " (" + session.level + ")");
			holder.text2.setTextColor(Color.parseColor(session.color));
		}
		if (holder.image != null) {
			try {
				holder.image.setImageURL(new URL(session.photo));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return convertView;
	}

	public class MyDrawable extends Drawable {

		private Paint mPainter;

		public MyDrawable(String color) {
			mPainter = new Paint();
			mPainter.setAntiAlias(true);
			mPainter.setColor(Color.parseColor(color));

		}

		@Override
		public void setColorFilter(ColorFilter cf) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setAlpha(int alpha) {
			// TODO Auto-generated method stub

		}

		@Override
		public int getOpacity() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void draw(Canvas canvas) {
			float density = getContext().getResources().getDisplayMetrics().density;
			Rect rect = getBounds();
			RectF rectF = new RectF(rect);
			canvas.drawRoundRect(rectF, 5 * density, 5 * density, mPainter);
			rect.bottom = (int) (rect.bottom - (5 * density));
			canvas.drawRect(rect, mPainter);

		}
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		// The header, selectable item and the instruction
		return 1;
	}

	private static class ViewHolder {

		public TextView text2;

		public TextView text1;

		public CircularImageView image;

	}

}
