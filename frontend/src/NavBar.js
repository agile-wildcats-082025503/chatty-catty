import React, { useContext, useEffect } from 'react';
import { Link, NavLink } from 'react-router-dom';

export default function NavBar() {

  return (
    <nav class="navbar">
      <img src="chatty-catty-logo_200x200.jpg"/>
      <ul>
        <li>
          <Link to="/">ChatBot</Link>
        </li>
        <li>
          <Link to="/Upload">Upload</Link>
        </li>
        <li>
          <Link to="/About">About</Link>
        </li>
      </ul>
    </nav>
  );
}