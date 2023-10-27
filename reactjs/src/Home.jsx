import React, {useState} from 'react';
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import CollapsibleFaq from "./component/CollapsibleFaq";

function Home() {



    let [faqData, setFaqData] = useState([
        {
            title: "What is Seebie?",
            content: "Seebie is a sleep diary app that helps you get better sleep, no device necessary.",
            collapsed: true
        },
        {
            title: "Why should I track my sleep?",
            content: "Keeping track of your sleep yourself can increase your self awareness and encourage healthy sleep behavior.",
            collapsed: true
        },
        {
            title: "How does this improve my sleep?",
            content: "You can track your sleep baseline, try a new evening routine, then" +
                "compare the two time periods to see how the new routine affected your sleep. " +
                "If you can see the improvement, it will help motivate you to keep that routine!",
            collapsed: true
        },
        {
            title: "How long does this take to improve my sleep?",
            content: "You should spend at least two weeks establishing your baseline sleep," +
            "and at least two weeks per experiment to compare the effectiveness of different sleep habits.",
            collapsed: true
        },
        {
            title: "What is Seebie?",
            content: "Seebie is a sleep diary app that helps you get better sleep, no device necessary.",
            collapsed: true
        }
    ]);

    // TODO allow HTML for bullets inside the content?

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

    // TODO button should be distinguishable from the text

    // TODO the collapse animation is not smooth, is it copying too much data? It's otherwise the same as the histogram


    function onToggleCollapse(i) {
        let newfaqData = structuredClone(faqData);
        newfaqData[i].collapsed = ! faqData[i].collapsed;
        setFaqData(newfaqData);
    }

    return (
        <div className="container mt-3 overflow-y-scroll h-90vh">

            {
                faqData.map((faq, i) => {
                    return (
                        <Row key={i}>
                            <Col className="col-12 ">
                                <CollapsibleFaq
                                    title={faq.title}
                                    content={faq.content}
                                    collapsed={faq.collapsed}
                                    onCollapseClick={() => onToggleCollapse(i)}
                                />
                            </Col>
                        </Row>
                    )
                })
            }


        </div>
    );

}

export default Home;
