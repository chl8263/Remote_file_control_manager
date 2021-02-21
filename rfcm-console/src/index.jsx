import React from 'react';
import ReactDom from 'react-dom';
import {Provider} from 'react-redux';
import { hot } from 'react-hot-loader/root';
import { Beforeunload } from 'react-beforeunload';

import App from'./routes/App';
import store from './store';

import { CookiesProvider } from 'react-cookie';

const Hot = hot(App);

ReactDom.render(
    <CookiesProvider>
        <Provider store={store}>
            <Hot />
        </Provider>
    </CookiesProvider>
    , document.querySelector('#root')
);