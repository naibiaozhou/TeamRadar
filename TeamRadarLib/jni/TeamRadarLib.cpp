#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <android/log.h>
#include "aeslib.h"
#define LOG_TAG "TeamRadarLib"
#define ALOGE(...)   __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
unsigned char iv[] = {
	0x22, 0x10, 0x19, 0x64,
	0x10, 0x19, 0x64, 0x22,
	0x19, 0x64, 0x22, 0x10,
	0x64, 0x22, 0x10, 0x19
};
unsigned char app_magic[16]={0x34,0x98,0xde,0xa5,0x50,0x80,0xce,0x1b,0xe9,0x6e,0x9d,0x66,0x74,0xd5,0x4c,0x77};
char key[]={0x24,0x08,0xe8,0x89,0x6d,0x00,0x00,0xa1,
			0x18,0xc0,0x40,0x00,0x85,0xc0,0x74,0x42,
			0x8b,0x1d,0x80,0xd1,0x40,0x00,0x89,0x44,
			0x43,0x24,0xe8,0x5a,0x6d,0xa1,0x18,0xe4};
static char string1[]={0x5c,0x4b,0x24,0x2c,0xc9,0x1c,0x76,0xca,0xee,0x06,0xb1,0x94,0xa2,0x3d,0x59,0x71};
static char string2[]={0x1c,0x67,0xbf,0x7a,0xb9,0x3b,0x38,0x86,0xd5,0x71,0xd5,0xda,0xfb,0x71,0x77,0x01,0x34,0x18,0x39,0xe9,0xe8,0x7b,0x75,0x24,0x14,0x44,0x46,0xe2,0xfa,0x1b,0x16,0xdb};
static char string3[]={0xbf,0x0b,0x7e,0x84,0x9b,0x03,0xdb,0x0d,0xd2,0xbf,0x0e,0xdc,0x6e,0xb1,0xf8,0x4a,0x66,0xbf,0x7f,0x96,0x7d,0x57,0xca,0xf3,0xa1,0x97,0x32,0x2b,0xf0,0x56,0x0b,0x78,0xd8,0xdd,0xa5,0x9d,0x01,0xd4,0xb8,0x90,0x82,0x1e,0x81,0xdd,0x8b,0xcb,0xc9,0x7b};
static char string4[]={0x77,0xc0,0x9c,0xc4,0xe4,0x08,0x61,0x4a,0xd8,0x49,0x1a,0xdc,0xf3,0x3c,0x57,0x94};
static char string5[]={0x0c,0xf3,0x46,0x87,0x2a,0x84,0xbc,0xb6,0xbf,0x16,0x20,0xfd,0x5e,0x53,0xce,0x0d,0xf7,0x38,0x94,0x2f,0x6f,0x66,0xcd,0x1f,0x2e,0x4c,0x76,0xb8,0xd7,0x4d,0x6d,0x81,0x6e,0xd3,0xb9,0xf4,0x78,0xf4,0x4d,0x76,0xcb,0x24,0x1e,0x9e,0x80,0x85,0x5d,0xa0};
static char string6[]={0xc2,0x42,0xb3,0x50,0x56,0x0c,0x70,0x34,0x44,0x23,0xbe,0x7d,0xf0,0x02,0x55,0xcf,0xe1,0x58,0xa8,0x8c,0x0f,0x36,0x4a,0x70,0xac,0x12,0xbb,0x0d,0x34,0x12,0xd2,0x84,0xa6,0x72,0xf9,0xa1,0x06,0xb2,0x3d,0x40,0x7e,0x0e,0x4f,0xd3,0x4a,0x7a,0x19,0x69};
static char string7[]={0x4f,0x52,0x0f,0x0f,0x6e,0x5c,0x32,0x5b,0x78,0xca,0xf8,0x2c,0x2d,0x79,0xdc,0x1c};
int chartostring(char * src,char *dest,int len)
{
	int i=0;
	memcpy(dest,src,len);
	decryptCBC((unsigned char *)dest, len,
			   (unsigned char *)key, 32, iv);
	return strlen(dest);
}

extern "C" {
	JNIEXPORT jint JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_setMagic(JNIEnv *env,jobject obj,jcharArray magic ,jint len);
	JNIEXPORT jstring JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_getString1(JNIEnv *env,jobject obj);
	JNIEXPORT jstring JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_getString2(JNIEnv *env,jobject obj);
	JNIEXPORT jstring JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_getString3(JNIEnv *env,jobject obj);
	JNIEXPORT jstring JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_getString4(JNIEnv *env,jobject obj);
	JNIEXPORT jstring JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_getString5(JNIEnv *env,jobject obj);
	JNIEXPORT jstring JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_getString6(JNIEnv *env,jobject obj);
	JNIEXPORT jstring JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_getString7(JNIEnv *env,jobject obj);
};

static int enable = 0;

int machMagic(unsigned char *magic,int length)
{
	int ret = 1;
	int i;
	if(length == 16)
	{
		for(i=0;i<16;i++)
		{
			if(magic[i]!=app_magic[i])
			{
				ret = 0;
			}
		}
	}
	else
	{
		ret = 0;
	}
	return ret;
}
int jchararraytochararray(jchar *src, char *dest,int len)
{
	if(len ==0 || src == NULL || dest == NULL)
		return -1;
	for(int i=0 ;i<len;i++)
	{
		dest[i]=(char)src[i];
	}
	dest[len] = 0;
	return 1;
}

JNIEXPORT jint JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_setMagic(JNIEnv *env,jobject obj,jcharArray magic ,jint len)
{
	jchar *pMagic;
	jboolean flag = true;
	char *myMagic = (char *)malloc(len+1);
	if(myMagic == NULL)
		return 0;
	pMagic= env->GetCharArrayElements(magic,&flag);
	if(pMagic == NULL)
	{
		free(myMagic);
		return 0;
	}
	jchararraytochararray(pMagic,myMagic,len);
	if(machMagic((unsigned char *)myMagic,len) == 1)
	{
		enable =1;
	}
	env->ReleaseCharArrayElements(magic,pMagic,0);
	free(myMagic);
	return 1;
}
/*
 * Class:     com_nut_teamradarlib_TeamRadarAPI
 * Method:    locSetSUPLServer
 * Signature: ([C)I
 */
JNIEXPORT jstring JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_getString1(JNIEnv *env,jobject obj)
{
	char buf[1024];
	memset(buf,0,1024);
	if(enable == 1)
	{
		chartostring(string1,buf,sizeof(string1));
	    return env->NewStringUTF(buf);
	}
	return env->NewStringUTF("theif");
}
JNIEXPORT jstring JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_getString2(JNIEnv *env,jobject obj)
{
	char buf[1024];
	memset(buf,0,1024);
	if(enable == 1)
	{
		chartostring(string2,buf,sizeof(string2));
	    return env->NewStringUTF(buf);
	}
	return env->NewStringUTF("theif");
}
JNIEXPORT jstring JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_getString3(JNIEnv *env,jobject obj)
{
	char buf[1024];
	memset(buf,0,1024);
	if(enable == 1)
	{
		chartostring(string3,buf,sizeof(string3));
	    return env->NewStringUTF(buf);
	}
	return env->NewStringUTF("theif");
}
JNIEXPORT jstring JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_getString4(JNIEnv *env,jobject obj)
{
	char buf[1024];
	memset(buf,0,1024);
	if(enable == 1)
	{
		chartostring(string4,buf,sizeof(string4));
	    return env->NewStringUTF(buf);
	}
	return env->NewStringUTF("theif");
}
JNIEXPORT jstring JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_getString5(JNIEnv *env,jobject obj)
{
	char buf[1024];
	memset(buf,0,1024);
	if(enable == 1)
	{
		chartostring(string5,buf,sizeof(string5));
	    return env->NewStringUTF(buf);
	}
	return env->NewStringUTF("theif");
}
JNIEXPORT jstring JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_getString6(JNIEnv *env,jobject obj)
{
	char buf[1024];
	memset(buf,0,1024);
	if(enable == 1)
	{
		chartostring(string6,buf,sizeof(string6));
	    return env->NewStringUTF(buf);
	}
	return env->NewStringUTF("theif");
}
JNIEXPORT jstring JNICALL Java_com_nut_teamradarlib_TeamRadarAPI_getString7(JNIEnv *env,jobject obj)
{
	char buf[1024];
	memset(buf,0,1024);
	if(enable == 1)
	{
		chartostring(string7,buf,sizeof(string7));
	    return env->NewStringUTF(buf);
	}
	return env->NewStringUTF("theif");
}
