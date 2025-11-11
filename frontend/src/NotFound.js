import React, { useContext, useEffect } from 'react';

export default function NotFound() {
  const possibleTitles = [
    "Well, how did I get here?",
    "Whatever you were looking for is not here.",
    "These are not the droids you're looking for.",
    "Hmph, not found.",
    "Maybe you should ask ChattyCatty how to find it?",
    "ChattyCatty is not amused.",
    "Are you lost?",
    "Here it is! Wait...",
    "I can't believe you found me!",
    "Oh noes! You broked it!! 🤣",
    "Keep looking... 👀",
    "Click a link above to get back on track.",
    "ChattyCatty frowns and says, \"You should not be here.\"",
    "You've been 404'ed!",
    "I've ... got nothing ...",
    "Wait -- how did you get here?",
    "By now, you may have discovered the text changes. Have fun!",
  ];
  const randomIndex = Math.floor(Math.random() * possibleTitles.length);
  const pageTitle =  possibleTitles[randomIndex];

  useEffect(() => {
    document.title = pageTitle;
  }, [pageTitle]); // The effect re-runs when pageTitle changes

  return (
    <div>
      <h1>404: {pageTitle}</h1>
      <center><img src="chatty-catty-not-found_400x400.jpg" /></center>
    </div>
  );
}
