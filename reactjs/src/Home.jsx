import React from 'react';

function Home() {

    return (
        <div className="container mt-3 overflow-y-scroll h-90vh">

            <h5>What is Seebie?</h5>
            <p>
                Seebie is a sleep diary app that helps you get better sleep, no device necessary.
            </p>

            <h5>Why should I track my sleep?</h5>
            <p>
                Keeping track of your sleep yourself can increase your self awareness and encourage healthy sleep behavior.
            </p>

            <h5>How does this improve my sleep?</h5>
            <p>
                You can track your sleep baseline, try a new evening routine, then
                compare the two time periods to see how the new routine affected your sleep.
                If you can see the improvement, it will help motivate you to keep that routine!
            </p>

            <h5>How long does it take to help my sleep?</h5>
            <p>
                You should spend at least two weeks establishing your baseline sleep,
                and at least two weeks per experiment to compare the effectiveness of
                different sleep habits.
            </p>


            <h5>
                What are some routines I can try?
            </h5>
            <p>
                <ul>
                    <li>Go to bed at the same time every day, even on weekends</li>
                    <li>Do something quiet and relaxing in low light for 60 minutes before bed instead of looking at your phone or watching a show</li>
                    <li>Cut your caffeine in half, or finish all caffeine for the day before lunch</li>
                    <li>Don't look at the clock during the night</li>
                </ul>
            </p>

        </div>
    );

}

export default Home;
