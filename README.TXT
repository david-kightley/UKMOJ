The test can be run out of the JourneyPlanner class.  It will load a balance onto the card and run the
specified journeys from instructions.  The output is the journey taken and the fare for each leg.
At the end it prints out the balance on the card.

A unit test fully tests the functions of the card and the pricing library.

Note that you are allowed a single journey on the card that takes you into a negative balance.  However, after that
you cannot touch in (throws an exception).

Also, if you touch in then touch out at the same station, it assumes you took a circular journey within the single
zone where the station is located. ie. you will still be charged.

A few optimizations that could be added but would increase the lines of code written....
1.  Interface on the Oyster card that would allow different types of card to be used. (Payment Card)
2.  Better inheritance of stations and BusStops - eg.  Interchange interface, implemented by TubeStation, BusStop,
MainlineTrainStation, Bicycle, etc.
3.  More flexible zoning - currently hard-coded to be only zones 1-3.

