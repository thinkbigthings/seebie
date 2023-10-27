import React, {useState} from 'react';
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import CollapsibleFaq from "./component/CollapsibleFaq";
import Container from "react-bootstrap/Container";

function Home() {


    const [faqData, setFaqData] = useState([
        {
            title: "What is Seebie?",
            content: "Seebie is a sleep diary app that helps you get better sleep, no device necessary.",
        },
        {
            title: "Why should I track my sleep?",
            content: "Keeping track of your sleep yourself can increase your self awareness and encourage healthy sleep behavior.",
        },
        {
            title: "How does this improve my sleep?",
            content: "You can track your sleep baseline, try a new evening routine, then" +
                "compare the two time periods to see how the new routine affected your sleep. " +
                "If you can see the improvement, it will help motivate you to keep that routine!",
        },
        {
            title: "How long does it take to improve sleep?",
            content: "You should spend at least two weeks establishing your baseline sleep," +
            "and at least two weeks per experiment to compare the effectiveness of different sleep habits.",
        },
        {
            title: "How long should I use this app?",
            content: "Old habits die hard, it’s still good to track your sleep to maintain your acquired habits " +
                "and keep you on track. Maintaining your sleep habits is just as important as establishing them.",
        }
    ]);




    // TODO allow HTML for bullets inside the content

    {/*<h5>*/}
    {/*    What are some routines I can try?*/}
    {/*</h5>*/}
    {/*<div className={"card card-body mb-4"}>*/}
    {/*    <ul>*/}
    {/*        <li>Go to bed at the same time every day, even on weekends</li>*/}
    {/*        <li>Do something quiet and relaxing in low light for 60 minutes before bed instead of looking at your phone or watching a show</li>*/}
    {/*        <li>Cut your caffeine in half, or finish all caffeine for the day before lunch</li>*/}
    {/*        <li>Don't look at the clock during the night</li>*/}
    {/*    </ul>*/}
    {/*</div>*/}

    // TODO button should have a consistent background color
    // whether just clicked or not, and different from the content background color

    // TODO simplify the collapse state handling for histogram?


    return (
        <Container className="overflow-y-scroll h-90vh ">

            {
                faqData.map((faq, i) => {
                    return (
                        <Row key={i}>
                            <Col className="col-12 ">
                                <CollapsibleFaq
                                    title={faq.title}
                                    content={faq.content}
                                />
                            </Col>
                        </Row>
                    )
                })
            }


        </Container>
    );

}

export default Home;
