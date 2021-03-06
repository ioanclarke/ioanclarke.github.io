import './App.css';
import {BrowserRouter as Router, Route, Switch} from "react-router-dom";

import Intro from './components/Intro'
import Projects from './components/Projects'
import Bio from './components/Bio'
import Menu from "./components/Menu";

const App = () => {

    window.onload = () => {
        const menu = document.getElementById('menu')
        const nameBox = document.getElementById('nameBox');
        if (nameBox !== null) {
            const introTextContainer = document.querySelector('.typewriter h1')
            const typewriterAnimationDuration = 2000
            const timeUntilNameBoxNarrows = typewriterAnimationDuration + 400;
            const timeUntilMenuAppears = timeUntilNameBoxNarrows + 1000;

            nameBox.style.width = 100 + '%'
            setTimeout(() => {
                nameBox.style.transition = 'width 1s ease-out'
                nameBox.style.width = 95 + '%'

            }, timeUntilNameBoxNarrows)

            setTimeout(() => {
                introTextContainer.style.animation = 'none'
                introTextContainer.style.borderRight = '0.1em solid transparent'
            }, typewriterAnimationDuration + 200)

            setTimeout(() => {
                menu.style.display = 'flex'
                // const menuItems = document.getElementsByClassName('menu-item')
                // for (const item of menuItems) {
                //     const itemWidth = item.style.width
                //     item.style.height = itemWidth + 'px'
                // }

            }, timeUntilMenuAppears)

        } else {
            menu.style.display = 'flex'
        }
    }
    const wrapperStyle = {
        backgroundColor: "rgb(0, 0, 53)",
        height: "100vh",
        width: "100vw"
    }

    const contentBoxClassName = 'contentBox'
    // const pages = ['home', 'projects', 'bio', 'intro']
    // const routes = []
    // let ComponentName
    // let path
    // for (const page of pages) {
    //     path = (page === 'intro') ? '/' : `/${page}`
    //     ComponentName = page[0].toUpperCase() + page.slice(1)
    //     routes.push(
    //         {
    //             path: path,
    //             name: <ComponentName divClassName={contentBoxClassName}/>
    //         }
    //     )
    // }

    const routes = [
        // {
        //     path: '/home',
        //     name: <Home divClassName={contentBoxClassName}/>
        // },
        {
            path: '/projects',
            name: <Projects divClassName={contentBoxClassName}/>
        },
        {
            path: '/bio',
            name: <Bio divClassName={contentBoxClassName}/>
        },
        {
            path: '/',
            name: <Intro divClassName={contentBoxClassName}/>
        }
    ]
    return (
        <Router>
            <div id="content-wrapper" style={wrapperStyle}>
                <div id='main'>
                    <Switch>
                        {routes.map((route, i) =>
                            <Route key={i} path={route.path}> {route.name} </Route>)}
                        {/*<Route path='/projects'><Projects/></Route>*/}
                        {/*<Route path='/bio'><Bio/></Route>*/}
                        {/*<Route path='/home'><Home firstTime={firstTime}/></Route>*/}
                        {/*<Route path='/'><Intro/></Route>*/}
                    </Switch>
                </div>
                <Menu/>
            </div>
        </Router>

    )
}

export default App;
