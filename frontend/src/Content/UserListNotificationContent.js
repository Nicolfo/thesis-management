import { useCallback, useContext, useEffect, useState } from "react";
import { Card, Col, Collapse, Image, Row } from "react-bootstrap";
import API from "../API/Api";
import { AuthContext } from "react-oauth2-code-pkce";
import parse from 'html-react-parser';
import dayjs from "dayjs";

function UserListNotificationContent() {
    const [notificationList, setNotificationList] = useState([]);
    const [expandedId, setExpandedId] = useState(-1);
    const {tokenData, token, login, logOut, loginInProgress, error} = useContext(AuthContext);

    useEffect(() => {
        const getNotifications = async () => {
            try {
                const list = await API.getAllNotificationsOfCurrentUser(token);
                list.sort((a, b) => dayjs(b.timestamp).diff(dayjs(a.timestamp)));
                setNotificationList(list);
            } catch (e) {
                console.log(e);
            }
        };
        getNotifications();
    }, [])

    return (
        <>
        <h1 className="mb-3">Notifications</h1>
        { notificationList.length === 0 && <>Your notifications will be shown here.</> }
        { notificationList.length > 0 && notificationList.map(n => <NotificationRow notification={n} key={n.id} id={n.id} expandedId={expandedId} setExpandedId={setExpandedId} setNotificationList={setNotificationList} token={token} />) }
        </>
    )
}

function NotificationRow({ id, notification, expandedId, setExpandedId, setNotificationList, token }) {
    const toggleOpen = async () => {
        setExpandedId(id);
        // Set notification read if not read yet
        if (!notification.read) {
            setNotificationList(l => {
                const newList = l.map(n => ({...n}));
                for (const n of newList) {
                    if (n.id === id) {
                        n.read = true;
                    }
                }
                return newList;
            });
            try {
                await API.markNotificationAsRead(token, id);
            } catch (e) {
                console.log(e);
            }
        }
    };
    return (
        <Card className="mb-2">
            <Card.Header className="list-elem-title" onClick={toggleOpen}>
                <Row>
                <Col md={6} xs={12}><Image className="me-2 d-none d-sm-inline" src={`notification_images/${notification.icon}`} width={25} height={25} />{ !notification.read ? <b>{notification.title}</b> : <>{notification.title}</> }</Col>
                <Col md={6} className="text-end d-none d-sm-block">{dayjs(notification.timestamp).format("MMM D, YYYY h:mm A")}</Col>
                </Row>
                
            </Card.Header>
            <Collapse in={id === expandedId}>
            <Card.Body className="pb-2">
            {parse(notification.text)}
            </Card.Body>
        </Collapse>
        </Card>
        
    )
} 

export default UserListNotificationContent;