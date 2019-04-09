package entity;

import java.util.List;

import com.myshop.pojo.TbGoods;
import com.myshop.pojo.TbGoodsDesc;
import com.myshop.pojo.TbItem;

public class Goods {
	private TbGoods goods;
	private TbGoodsDesc goodsDesc;
	private List<TbItem> itemList;
	public TbGoods getGoods() {
		return goods;
	}
	public void setGoods(TbGoods goods) {
		this.goods = goods;
	}
	public TbGoodsDesc getGoodsDesc() {
		return goodsDesc;
	}
	public void setGoodsDesc(TbGoodsDesc goodsDesc) {
		this.goodsDesc = goodsDesc;
	}
	public List<TbItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<TbItem> itemList) {
		this.itemList = itemList;
	}
}
