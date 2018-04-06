/**
 * @file  SampleContentViewController.h
 * @brief CameraRemoteSampleApp
 *
 * Copyright 2014 Sony Corporation
 */

#import "SampleStreamingDataManager.h"
#import "HttpAsynchronousRequest.h"
#import "SampleCameraEventObserver.h"

@interface SampleContentViewController
    : UIViewController <SampleStreamingDataDelegate,
                        HttpAsynchronousRequestParserDelegate,
                        SampleEventObserverDelegate>

@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (nonatomic) NSString *contentFileName;
@property (nonatomic) NSString *contentUri;
@property (nonatomic) NSString *contentUrl;
@property (nonatomic) NSString *contentKind;

@end
