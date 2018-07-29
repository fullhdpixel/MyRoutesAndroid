@Override
public boolean onTouchEvent(MotionEvent event, MapView mapView) {

final int action=event.getAction();
final int x=(int)event.getX();
final int y=(int)event.getY();
boolean result=false;
      if (action==MotionEvent.ACTION_DOWN) {
        for (OverlayItem item : mOverlays) {
          Point p=new Point(0,0);

          mapa.getProjection().toPixels(item.getPoint(), p);

              //I maintain the hitTest's bounds so you can still
              //press near the marker
          if (hitTest(item, marker, x-p.x, y-p.y)) {
            result=true;

            inDrag=item;

            mOverlays.remove(inDrag);
            populate();

                //Instead of using the DragImageOffSet and DragTouchOffSet
                //I use the x and y coordenates from the Point
            setDragImagePosition(x, y);

            dragImage.setVisibility(View.VISIBLE);

            break;
          }
        }
      }
      else if (action==MotionEvent.ACTION_MOVE && inDrag!=null) {
        setDragImagePosition(x, y);

        result=true;
      }
      else if (action==MotionEvent.ACTION_UP && inDrag!=null) {
        dragImage.setVisibility(View.GONE);

            //I get the geopoint without using any OffSet, directly with the 
            //Point coordenates
        GeoPoint pt=mapa.getProjection().fromPixels(x,y);

        OverlayItem toDrop=new OverlayItem(pt, inDrag.getTitle(),
                                           inDrag.getSnippet());
        orig = inDrag.getMarker(0);

            //In my case I had down heading Arrows as markers, so I wanted my 
            //bounds to be at the center bottom
        toDrop.setMarker(boundCenterBottom(orig));

        mOverlays.add(toDrop);
        populate();

        inDrag=null;
        result=true;
      }


   return(result || super.onTouchEvent(event, mapView));
}

private void setDragImagePosition(int x, int y) {
  RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)dragImage.getLayoutParams();

  //Instead of using OffSet I use the Point coordenates.
  //I want my arrow to appear pointing to the point I am moving, so 
  //I set the margins as the Point coordenates minus the Height and half the
  //width of my arrow.
  lp.setMargins(x-(dragImage.getWidth()/2),y-dragImage.getHeight(), 0, 0);
  dragImage.setLayoutParams(lp);
}