package com.nut.teamradar.util;

import java.util.Random;

import android.util.Log;

public class MyColor {
	private static final String TAG = "MyColor";
	private static Random RandomIndexGen =null;
	private static int ColorTable[][] = new int[][]{
		{0xff000000,0xff272727,0xff3C3C3C,0xff4F4F4F,0xff5B5B5B,0xff6C6C6C,0xff7B7B7B,0xff8E8E8E,0xff9D9D9D,0xffADADAD,0xffBEBEBE,0xffd0d0d0},
		{0xff000079,0xff003D79,0xff004B97,0xff005AB5,0xff0066CC,0xff0072E3,0xff0080FF,0xff2894FF,0xff46A3FF,0xff66B3FF,0xff84C1FF,0xff97CBFF},
		{0xff600030,0xff820041,0xff9F0050,0xffBF0060,0xffD9006C,0xffF00078,0xffFF0080,0xffFF359A,0xffFF60AF,0xffFF79BC,0xffFF95CA,0xffffaad5},
		{0xff460046,0xff5E005E,0xff750075,0xff930093,0xffAE00AE,0xffD200D2,0xffE800E8,0xffFF00FF,0xffFF44FF,0xffFF77FF,0xffFF8EFF,0xffffa6ff},
		{0xff28004D,0xff3A006F,0xff4B0091,0xff5B00AE,0xff6F00D2,0xff8600FF,0xff921AFF,0xff9F35FF,0xffB15BFF,0xffBE77FF,0xffCA8EFF,0xffd3a4ff},
		{0xff000079,0xff000093,0xff0000C6,0xff0000C6,0xff0000E3,0xff2828FF,0xff4A4AFF,0xff6A6AFF,0xff7D7DFF,0xff9393FF,0xffAAAAFF,0xffB9B9FF},
		{0xff003E3E,0xff005757,0xff007979,0xff009393,0xff00AEAE,0xff00CACA,0xff00E3E3,0xff00FFFF,0xff4DFFFF,0xff80FFFF,0xffA6FFFF,0xffBBFFFF},
		{0xff006030,0xff01814A,0xff019858,0xff01B468,0xff02C874,0xff02DF82,0xff02F78E,0xff1AFD9C,0xff4EFEB3,0xff7AFEC6,0xff96FED1,0xffADFEDC},
		{0xff424200,0xff5B5B00,0xff737300,0xff8C8C00,0xffA6A600,0xffC4C400,0xffE1E100,0xffF9F900,0xffFFFF37,0xffFFFF6F,0xffFFFF93,0xffFFFFAA},
		{0xff006000,0xff007500,0xff009100,0xff00A600,0xff00BB00,0xff00DB00,0xff00EC00,0xff28FF28,0xff53FF53,0xff79FF79,0xff93FF93,0xffA6FFA6},
		{0xff467500,0xff548C00,0xff64A600,0xff73BF00,0xff82D900,0xff8CEA00,0xff9AFF02,0xffA8FF24,0xffB7FF4A,0xffC2FF68,0xffCCFF80,0xffD3FF93},
		{0xff5B4B00,0xff796400,0xff977C00,0xffAE8F00,0xffC6A300,0xffD9B300,0xffEAC100,0xffFFD306,0xffFFDC35,0xffFFE153,0xffFFE66F,0xffFFED97},
		{0xff844200,0xff9F5000,0xffBB5E00,0xffD26900,0xffEA7500,0xffFF8000,0xffFF9224,0xffFFA042,0xffFFAF60,0xffFFBB77,0xffFFC78E,0xffFFD1A4},
		{0xff642100,0xff842B00,0xffA23400,0xffBB3D00,0xffD94600,0xffF75000,0xffFF5809,0xffFF8040,0xffFF8F59,0xffFF9D6F,0xffFFAD86,0xffFFBD9D},
		{0xff613030,0xff743A3A,0xff804040,0xff984B4B,0xffAD5A5A,0xffB87070,0xffC48888,0xffCF9E9E,0xffD9B3B3,0xffE1C4C4,0xffEBD6D6,0xffF2E6E6},
		{0xff616130,0xff707038,0xff808040,0xff949449,0xffA5A552,0xffAFAF61,0xffB9B973,0xffC2C287,0xffCDCD9A,0xffD6D6AD,0xffDEDEBE,0xffE8E8D0},
		{0xff336666,0xff3D7878,0xff408080,0xff4F9D9D,0xff5CADAD,0xff6FB7B7,0xff81C0C0,0xff95CACA,0xffA3D1D1,0xffB3D9D9,0xffC4E1E1,0xffD1E9E9},
		{0xff484891,0xff5151A2,0xff5A5AAD,0xff7373B9,0xff8080C0,0xff9999CC,0xffA6A6D2,0xffB8B8DC,0xffC7C7E2,0xffD8D8EB,0xffE6E6F2,0xffF3F3FA},
		{0xff6C3365,0xff7E3D76,0xff8F4586,0xff9F4D95,0xffAE57A4,0xffB766AD,0xffC07AB8,0xffCA8EC2,0xffD2A2CC,0xffDAB1D5,0xffE2C2DE,0xffEBD3E8}

	};
	private static MyColor _instance = null;
	private MyColor()
	{
		RandomIndexGen = new Random(0);
	}
	public static MyColor getInstance()
	{
		if(_instance == null)
			_instance = new MyColor();
		return _instance;
	}
	public int GetColorSeries()
	{
		int ret =RandomIndexGen.nextInt();
		if(ret < 0 )
			ret = 0-ret;
		ret %= 19;
		
		//Log.e(TAG,String.format("Set Serial =%d ", ret));
		return ret;
	}
	//speed km/s
	public int getColor(int ColorSerial,double speed)
	{
		int index=0;
		if(speed<=5)
		{
			index =0;
		}
		else if(speed > 5 && speed< 7)
		{
			index =1;
		}
		else if(speed >= 7 && speed< 9)
		{
			index =2;
		}
		else if(speed >= 9 && speed< 11)
		{
			index =3;
		}
		else if(speed >= 11 && speed< 13)
		{
			index =4;
		}
		else if(speed >= 13 && speed< 15)
		{
			index =5;
		}
		else if(speed >= 15 && speed< 19)
		{
			index =6;
		}
		else if(speed >= 19 && speed< 50)
		{
			index =7;
		}
		else if(speed >= 50 && speed< 100)
		{
			index =8;
		}
		else if(speed >= 100 && speed< 150)
		{
			index =9;
		}
		else if(speed >= 150 && speed< 250)
		{
			index =10;
		}
		else
		{
			index =11;
		}
		//Log.e(TAG,String.format("Get Serial =%d index=%d color=%06x", ColorSerial,index,ColorTable[ColorSerial][index]));
		return ColorTable[ColorSerial][index];
	}

}