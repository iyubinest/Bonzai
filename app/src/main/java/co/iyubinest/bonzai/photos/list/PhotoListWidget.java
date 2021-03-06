/**
 * Copyright (C) 2017 Cristian Gomez Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.iyubinest.bonzai.photos.list;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.iyubinest.bonzai.R;
import co.iyubinest.bonzai.photos.Photo;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class PhotoListWidget extends RecyclerView {

  private final PhotoListAdapter adapter;

  public PhotoListWidget(Context context) {
    this(context, null);
  }

  public PhotoListWidget(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    setLayoutManager(layoutManager);
    setHasFixedSize(true);
    SnapHelper snapHelper = new LinearSnapHelper();
    snapHelper.attachToRecyclerView(this);
    adapter = new PhotoListAdapter();
    setAdapter(adapter);
  }

  public void onPhotoSelected(PhotoListAdapter.OnPhotoSelected listener) {
    adapter.setListener(listener);
  }

  public void load(List<Photo> photoList) {
    adapter.add(photoList);
  }

  public static class PhotoListAdapter extends RecyclerView.Adapter<PhotoHolder> {

    private List<Photo> photoList = new ArrayList<>();
    private OnPhotoSelected listener;

    public void add(List<Photo> photoList) {
      this.photoList.clear();
      this.photoList.addAll(photoList);
      notifyDataSetChanged();
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list_item, parent, false);
      return new PhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoHolder holder, int position) {
      holder.load(photoList.get(position));
      holder.setListener((pos, view) -> {
        if (listener != null) {
          listener.selectedPhoto(photoList.get(pos), view);
        }
      });
    }

    @Override
    public int getItemCount() {
      return photoList.size();
    }

    public void setListener(OnPhotoSelected listener) {
      this.listener = listener;
    }

    interface OnPhotoSelected {

      void selectedPhoto(Photo photo, View view);
    }
  }

  static class PhotoHolder extends ViewHolder {

    @BindView(R.id.photo_list_item_image)
    ImageView imageView;
    private OnSelection listener;

    public PhotoHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }

    public void load(Photo photo) {
      Picasso.with(itemView.getContext()).load(photo.url()).fit().centerCrop().into(imageView);
    }

    @OnClick(R.id.photo_list_item_image)
    public void selected() {
      if (listener != null) {
        listener.positionSelected(getAdapterPosition(), imageView);
      }
    }

    public void setListener(OnSelection listener) {
      this.listener = listener;
    }

    interface OnSelection {

      void positionSelected(int position, View view);
    }
  }
}
