/**
 * @file  SampleStreamingDataManager.h
 * @brief CameraRemoteSampleApp
 *
 * Copyright 2014 Sony Corporation
 */

@protocol SampleStreamingDataDelegate <NSObject>

- (void)didFetchImage:(UIImage *)image;

- (void)didStreamingStopped;

@end

@interface SampleStreamingDataManager : NSObject <NSStreamDelegate>

- (void)start:(NSString *)url
 viewDelegate:(id<SampleStreamingDataDelegate>)viewDelegate;

- (void)stop;

- (BOOL)isStarted;

@end
