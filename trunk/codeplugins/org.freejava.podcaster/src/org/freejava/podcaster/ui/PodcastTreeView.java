package org.freejava.podcaster.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.freejava.podcaster.Activator;
import org.freejava.podcaster.dao.impl.Database;
import org.freejava.podcaster.domain.PodcastFeed;
import org.freejava.podcaster.domain.PodcastInfo;
import org.freejava.podcaster.domain.PodcastItem;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class PodcastTreeView extends ViewPart implements PlayingIconIndicator {
    private TreeViewer viewer;
    private DrillDownAdapter drillDownAdapter;
    private Action syncAction;
    private Action exportWPL;
    private Action playPauseClip;
    private Action playClip;
    private Action nextClip;
    //private Action previousClip;
    private PodcastPlaylist playlist = new PodcastPlaylist(this);
    private PodcastItem playingItem;
    private ImageRegistry ir = new ImageRegistry();

    class ViewContentProvider implements ITreeContentProvider {
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }

        public Object[] getElements(Object parent) {
            Object[] result;
            try {
                if (parent instanceof PodcastFeed) {
                    result = getChildren(parent);
                } else if (parent instanceof PodcastItem) {
                    result = new Object[0];
                } else {
                    result = Database.getInstance().getPodcastFeeds().toArray();
                }
            } catch (Exception e) {
                result = new Object[0];
                Activator.logError("Cannot get root nodes", e);
            }
            return result;
        }

        public Object getParent(Object child) {
            try {
                if (child instanceof PodcastItem) {
                    PodcastItem item = (PodcastItem) child;
                    for (PodcastFeed feed : Database.getInstance().getPodcastFeeds()) {
                        if (feed.getId().equals(item.getPodcastFeedId())) {
                            return feed;
                        }
                    }
                }
            } catch (Exception e) {
                Activator.logError("Cannot get parent", e);
            }
            return null;
        }

        public Object[] getChildren(Object parent) {
            try {
                if (parent instanceof PodcastFeed) {
                    PodcastFeed feed = (PodcastFeed) parent;
                    List<PodcastItem> chilren = new ArrayList<PodcastItem>();
                    for (PodcastItem item : Database.getInstance().getPodcastItems()) {
                        if (feed.getId().equals(item.getPodcastFeedId())) {
                            chilren.add(item);
                        }
                    }
                    return chilren.toArray();
                }
            } catch (Exception e) {
                Activator.logError("Cannot get chilren", e);
            }
            return new Object[0];
        }

        public boolean hasChildren(Object parent) {
            try {
                if (parent instanceof PodcastFeed) {
                    PodcastFeed feed = (PodcastFeed) parent;
                    for (PodcastItem item : Database.getInstance().getPodcastItems()) {
                        if (feed.getId().equals(item.getPodcastFeedId())) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                Activator.logError("Cannot get chilren", e);
            }

            return false;
        }

    }

    private Image getCacheImage(String path) {
        Image image = ir.get(path);
        if (image == null) {
            image = Activator.getImageDescriptor(path).createImage();
            ir.put(path, image);
        }
        return image;
    }
    class PodcastLabelProvider extends ColumnLabelProvider {
        private int index;

        public PodcastLabelProvider(int index) {
            this.index = index;
        }

        public Image getImage(Object element) {
            Image result = null;
            if (index == 0 && element instanceof PodcastFeed) {
                result = getCacheImage("icons/folder_music.png");
            } else if (index == 0 && element instanceof PodcastItem) {
                PodcastItem item = (PodcastItem) element;
                String type = item.getEnclosureType();
                if (item.equals(playingItem)) {
                    result = getCacheImage("icons/mix_audio.png");
                } else if (type != null && type.startsWith("video")) {
                    result = getCacheImage("icons/video.png");
                } else if (type != null && type.startsWith("audio")) {
                    result = getCacheImage("icons/audio.png");
                }
            } else if (index == 1 && element instanceof PodcastItem) {
                PodcastItem item = (PodcastItem) element;
                if (item.getEnclosureDiskpath() != null) {
                    result = getCacheImage("icons/harddisk.png");
                } else {
                    result = getCacheImage("icons/network.png");
                }
            }
            return result;
        }

        public String getText(Object element) {
            PodcastInfo podcastNode = (PodcastInfo) element;
            switch (index) {
            case 0: // Podcast column
                return podcastNode.getTitle();
            case 1: // Size
                if (podcastNode instanceof PodcastItem) {
                    PodcastItem item = (PodcastItem) podcastNode;
                    return String.valueOf(item.getEnclosureLength()
                            / (1024 * 1024))
                            + " MB";
                }
                return null;
            case 2: // Release Date
                String formattedDate = null;
                Date date = podcastNode.getPublishDate();
                if (date != null) {
                    formattedDate = FastDateFormat.getInstance("MM/dd/yy HH:mm").format(date);
                }
                return formattedDate;
            case 3: // Description
                return podcastNode.getDescription();
            }

            return null;
        }
    }

    class ViewLabelProvider extends LabelProvider {
        public Image getImage(Object element) {
            return new PodcastLabelProvider(0).getImage(element);
        }

        public String getText(Object element) {
            return new PodcastLabelProvider(0).getText(element);
        }
    }

    class PodcastViewerComparator extends ViewerComparator {
        public int compare(Viewer viewer, Object e1, Object e2) {
            if (e1 instanceof PodcastItem && e2 instanceof PodcastItem) {
                PodcastItem item1 = (PodcastItem) e1;
                PodcastItem item2 = (PodcastItem) e2;
                Date date1 = item1.getPublishDate();
                Date date2 = item2.getPublishDate();
                Date current = new Date();
                if (date1 == null) date1 = current;
                if (date2 == null) date2 = current;
                return date1.compareTo(date2);
            }
            if (e1 instanceof PodcastFeed && e2 instanceof PodcastFeed) {
                PodcastFeed feed1 = (PodcastFeed) e1;
                PodcastFeed feed2 = (PodcastFeed) e2;
                return feed1.getTitle().compareTo(feed2.getTitle());
            }
            return super.compare(viewer, e1, e2);
        }
    }

    /**
     * The constructor.
     */
    public PodcastTreeView() {
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent) {

        try {
            Database.getInstance().initialize();
        } catch (Exception e) {
            Activator.logError("Cannot initialize database", e);
        }

        viewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.H_SCROLL
                | SWT.V_SCROLL);
        drillDownAdapter = new DrillDownAdapter(viewer);
        viewer.setContentProvider(new ViewContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());
        viewer.setComparator(new PodcastViewerComparator());
        viewer.setInput(getViewSite());

        Tree tree = viewer.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        // tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        TreeViewerColumn podcast = new TreeViewerColumn(viewer, SWT.NONE);
        podcast.getColumn().setText("Podcast");
        podcast.getColumn().setWidth(300);
        podcast.setLabelProvider(new PodcastLabelProvider(0));
        TreeViewerColumn size = new TreeViewerColumn(viewer, SWT.NONE);
        size.getColumn().setText("Size");
        size.getColumn().setWidth(70);
        size.setLabelProvider(new PodcastLabelProvider(1));
        TreeViewerColumn date = new TreeViewerColumn(viewer, SWT.NONE);
        date.getColumn().setText("Release Date");
        date.getColumn().setWidth(120);
        date.setLabelProvider(new PodcastLabelProvider(2));
        TreeViewerColumn desc = new TreeViewerColumn(viewer, SWT.NONE);
        desc.getColumn().setText("Description");
        desc.setLabelProvider(new PodcastLabelProvider(3));
        desc.getColumn().setWidth(100);

        //viewer.expandAll();

        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                PodcastTreeView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(syncAction);
        manager.add(new Separator());
        manager.add(exportWPL);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(syncAction);
        manager.add(exportWPL);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(syncAction);
        manager.add(exportWPL);
        manager.add(playPauseClip);
        manager.add(nextClip);
        //manager.add(previousClip);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
    }

    private void makeActions() {
        syncAction = new Action() {
            public void run() {

                FeedUpdateJob job1 = new FeedUpdateJob(Database.getInstance());
                job1.addJobChangeListener(new JobChangeAdapter() {
                    public void done(IJobChangeEvent event) {
                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {
                                PodcastTreeView.this.viewer.setInput(getViewSite());
                            }
                        });
                    }
                });
                job1.schedule();

                VOAUpdateJob job2 = new VOAUpdateJob(Database.getInstance());
                job2.addJobChangeListener(new JobChangeAdapter() {
                    public void done(IJobChangeEvent event) {
                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {
                                PodcastTreeView.this.viewer.setInput(getViewSite());
                            }
                        });
                    }
                });
                job2.schedule();
            }
        };
        syncAction.setText("Synchronize All Podcasts");
        syncAction.setToolTipText("Synchronize All Podcasts");
        syncAction.setImageDescriptor(Activator
                .getImageDescriptor("icons/reload.png"));

        exportWPL = new Action() {
            public void run() {
                ExportPodcastItemsJob myJob = new ExportPodcastItemsJob();
                myJob.schedule();
            }
        };
        exportWPL.setText("Export to Playlists");
        exportWPL
                .setToolTipText("Export Podcast Feeds to Windows Media Playlists");
        exportWPL.setImageDescriptor(Activator
                .getImageDescriptor("icons/fileexport.png"));

        playPauseClip = new Action() {
            public void run() {
                if (StringUtils.equals(this.getText(), "Play")) {
                    if (playlist.isPaused()) {
                       playlist.resume();
                    } else {
                        PodcastFeed feed;
                        PodcastItem item;
                        ISelection selection = viewer.getSelection();
                        Object  obj = ((IStructuredSelection) selection).getFirstElement();
                        if (obj instanceof PodcastFeed) {
                            feed = (PodcastFeed) obj;
                            item = null;
                        } else {
                            feed = (PodcastFeed) new ViewContentProvider()
                                    .getParent(obj);
                            item = (PodcastItem) obj;
                        }
                        playlist.play(feed, item);
                    }
                    setText("Pause");
                    setImageDescriptor(Activator.getImageDescriptor("icons/player_pause.png"));
                } else {
                    playlist.pause();
                    setText("Play");
                    setImageDescriptor(Activator.getImageDescriptor("icons/player_play.png"));
                }
            }
        };
        playPauseClip.setText("Play");
        playPauseClip.setImageDescriptor(Activator.getImageDescriptor("icons/player_play.png"));

        playClip = new Action() {
            public void run() {
                    ISelection selection = viewer.getSelection();
                    Object  obj = ((IStructuredSelection) selection).getFirstElement();
                    PodcastFeed feed;
                    PodcastItem item;
                    if (obj instanceof PodcastFeed) {
                        feed = (PodcastFeed) obj;
                        item = null;
                    } else {
                        feed = (PodcastFeed) new ViewContentProvider()
                                .getParent(obj);
                        item = (PodcastItem) obj;
                    }
                    playlist.play(feed, item);
                    playPauseClip.setText("Pause");
                    playPauseClip.setImageDescriptor(Activator.getImageDescriptor("icons/player_pause.png"));

            }
        };
        playClip.setText("Play");
        playClip.setImageDescriptor(Activator.getImageDescriptor("icons/player_play.png"));


        nextClip = new Action() {
            public void run() {
                playlist.next();
            }
        };
        nextClip.setText("Next");
        nextClip.setImageDescriptor(Activator
                .getImageDescriptor("icons/player_fwd.png"));

//        previousClip = new Action() {
//            public void run() {
//            	playlist.previous();
//            }
//        };
//        previousClip.setText("Previous");
//        previousClip.setImageDescriptor(Activator
//                .getImageDescriptor("icons/player_rew.png"));

    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                playClip.run();
            }
        });
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    @Override
    public void dispose() {
        this.playlist.stop();
        Database.getInstance().shutdown();
        super.dispose();
    }

    @Override
    public void hidePlayingIcon(PodcastItem currentItem) {
        PodcastItem tmp = this.playingItem;
        this.playingItem = null;
        refreshNode(tmp);
        refreshNode(currentItem);
    }

    @Override
    public void showPlayingIcon(PodcastItem feedItem) {
        if (this.playingItem != null) {
            PodcastItem temp = playingItem;
            this.playingItem = null;
            refreshNode(temp);
        }
        this.playingItem = feedItem;
        refreshNode(feedItem);
    }

    private void refreshNode(final PodcastItem currentItem) {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                viewer.refresh(currentItem);
            }
        });
    }
}