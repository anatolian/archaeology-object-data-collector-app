/**
 * @file  SampleGalleryViewController.h
 * @brief CameraRemoteSampleApp
 *
 * Copyright 2014 Sony Corporation
 */

#import "HttpAsynchronousRequest.h"
#import "SampleCameraEventObserver.h"

@interface SampleGalleryViewController
    : UICollectionViewController <
          UICollectionViewDataSource, UICollectionViewDelegate,
          HttpAsynchronousRequestParserDelegate, SampleEventObserverDelegate>
@property (weak, nonatomic) IBOutlet UICollectionView *galleryView;
@property (nonatomic) BOOL isMovieAvailable;
@property (nonatomic) NSString *dateUri;
@property (nonatomic) NSString *dateTitle;

@end
